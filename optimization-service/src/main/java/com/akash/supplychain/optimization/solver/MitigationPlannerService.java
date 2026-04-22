package com.akash.supplychain.optimization.solver;

import com.akash.supplychain.optimization.dto.InventoryRecommendationDto;
import com.akash.supplychain.optimization.dto.MitigationOptionDto;
import com.akash.supplychain.optimization.dto.MitigationPlanRequest;
import com.akash.supplychain.optimization.dto.MitigationPlanResponse;
import com.akash.supplychain.optimization.dto.RouteOptimizationRequest;
import com.akash.supplychain.optimization.dto.RouteRecommendationDto;
import com.akash.supplychain.optimization.dto.SupplierScoreDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MitigationPlannerService {

    private final SupplierOptimizerService supplierOptimizerService;
    private final RouteOptimizerService routeOptimizerService;
    private final InventoryOptimizerService inventoryOptimizerService;

    public MitigationPlannerService(SupplierOptimizerService supplierOptimizerService,
                                    RouteOptimizerService routeOptimizerService,
                                    InventoryOptimizerService inventoryOptimizerService) {
        this.supplierOptimizerService = supplierOptimizerService;
        this.routeOptimizerService = routeOptimizerService;
        this.inventoryOptimizerService = inventoryOptimizerService;
    }

    public MitigationPlanResponse plan(MitigationPlanRequest request) {
        String origin = request.origin() == null || request.origin().isBlank() ? "Taiwan" : request.origin();
        String destination = request.destination() == null || request.destination().isBlank() ? "Pune" : request.destination();

        List<SupplierScoreDto> rankedSuppliers = supplierOptimizerService.rankSuppliers(
                request.materialType(),
                request.severity(),
                request.candidateSuppliers()
        );
        RouteRecommendationDto recommendedRoute = routeOptimizerService.optimize(new RouteOptimizationRequest(
                origin,
                destination,
                request.severity(),
                request.exposureScore(),
                request.routeOptions()
        )).recommendedRoute();
        List<InventoryRecommendationDto> inventoryRecommendations = inventoryOptimizerService.recommendForPlan(
                request.materialType(),
                request.severity(),
                request.exposureScore(),
                request.inventoryPositions()
        );

        List<MitigationOptionDto> options = buildOptions(rankedSuppliers, recommendedRoute, inventoryRecommendations, request);

        return new MitigationPlanResponse(
                request.supplyChainId(),
                request.disruptionTitle(),
                request.materialType(),
                request.severity() == null ? "MEDIUM" : request.severity(),
                request.exposureScore() == null ? 50.0 : request.exposureScore(),
                LocalDateTime.now(),
                rankedSuppliers,
                recommendedRoute,
                inventoryRecommendations,
                options
        );
    }

    private List<MitigationOptionDto> buildOptions(List<SupplierScoreDto> suppliers,
                                                   RouteRecommendationDto route,
                                                   List<InventoryRecommendationDto> inventory,
                                                   MitigationPlanRequest request) {
        List<MitigationOptionDto> options = new ArrayList<>();
        SupplierScoreDto bestSupplier = suppliers.getFirst();
        InventoryRecommendationDto largestReplenishment = inventory.stream()
                .max(Comparator.comparing(InventoryRecommendationDto::replenishmentQuantity))
                .orElseThrow();

        options.add(new MitigationOptionDto(
                "SUPPLIER_SWITCH",
                "Qualify alternate supplier " + bestSupplier.name(),
                "Start emergency qualification and reserve capacity with " + bestSupplier.supplierCode() + ".",
                confidence(bestSupplier.optimizationScore()),
                bestSupplier.unitCost().multiply(BigDecimal.valueOf(500)).setScale(2, RoundingMode.HALF_UP),
                Math.max(5, bestSupplier.leadTimeDays() / 2),
                bestSupplier.optimizationScore() >= 82 ? "PROCUREMENT_MANAGER" : "SUPPLY_CHAIN_DIRECTOR",
                "Best ranked alternate supplier for " + request.materialType() + " under current disruption assumptions."
        ));

        options.add(new MitigationOptionDto(
                "REROUTE",
                "Move priority freight to " + route.routeId(),
                "Use route " + route.routeId() + " for high-priority shipments until the event stabilizes.",
                confidence(route.optimizationScore()),
                route.totalEstimatedCost(),
                route.totalTransitDays(),
                route.optimizationScore() >= 75 ? "LOGISTICS_MANAGER" : "OPERATIONS_DIRECTOR",
                route.rationale()
        ));

        options.add(new MitigationOptionDto(
                "INVENTORY_BUFFER",
                "Increase buffer at " + largestReplenishment.warehouseCode(),
                "Replenish " + largestReplenishment.replenishmentQuantity() + " units for " + largestReplenishment.materialSku() + ".",
                largestReplenishment.replenishmentQuantity().signum() > 0 ? 0.86 : 0.72,
                largestReplenishment.replenishmentQuantity().multiply(BigDecimal.valueOf(4.25)).setScale(2, RoundingMode.HALF_UP),
                largestReplenishment.replenishmentQuantity().signum() > 0 ? 3 : 1,
                "INVENTORY_CONTROLLER",
                largestReplenishment.rationale()
        ));

        options.add(new MitigationOptionDto(
                "EXECUTIVE_WATCH",
                "Create executive watch item for " + request.disruptionTitle(),
                "Track supplier status, port status, and inventory coverage every 6 hours.",
                0.78,
                BigDecimal.ZERO.setScale(2),
                1,
                "SUPPLY_CHAIN_MANAGER",
                "Keeps leadership aligned while automated agents continue tracing downstream impact."
        ));

        return options.stream()
                .sorted(Comparator.comparingDouble(MitigationOptionDto::confidenceScore).reversed())
                .limit(3)
                .toList();
    }

    private double confidence(double optimizationScore) {
        return Math.round(Math.min(0.95, Math.max(0.55, optimizationScore / 100.0)) * 100.0) / 100.0;
    }
}

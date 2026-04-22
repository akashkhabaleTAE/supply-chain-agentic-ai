package com.akash.supplychain.optimization.solver;

import com.akash.supplychain.optimization.dto.InventoryOptimizationRequest;
import com.akash.supplychain.optimization.dto.InventoryOptimizationResponse;
import com.akash.supplychain.optimization.dto.InventoryPositionDto;
import com.akash.supplychain.optimization.dto.InventoryRecommendationDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InventoryOptimizerService {

    public InventoryOptimizationResponse optimize(InventoryOptimizationRequest request) {
        List<InventoryPositionDto> positions = request.inventoryPositions() == null || request.inventoryPositions().isEmpty()
                ? fallbackInventory(request.materialType())
                : request.inventoryPositions();

        List<InventoryRecommendationDto> recommendations = positions.stream()
                .map(position -> recommend(position, request.severity(), request.exposureScore()))
                .toList();

        return new InventoryOptimizationResponse(request.materialType(), recommendations);
    }

    public List<InventoryRecommendationDto> recommendForPlan(String materialType, String severity,
                                                             Double exposureScore,
                                                             List<InventoryPositionDto> positions) {
        return optimize(new InventoryOptimizationRequest(materialType, severity, exposureScore, positions)).recommendations();
    }

    private InventoryRecommendationDto recommend(InventoryPositionDto position, String severity, Double exposureScore) {
        double available = position.availableQuantity().doubleValue();
        double demand = position.averageDailyDemand().doubleValue();
        double coverageDays = available / demand;
        int bufferDays = position.safetyStockDays() + RiskMath.riskBufferDays(severity, exposureScore);
        double targetQuantity = demand * (position.leadTimeDays() + bufferDays);
        double replenishment = Math.max(0, targetQuantity - available);

        String recommendation = replenishment > 0 ? "EXPEDITE_REPLENISHMENT" : "HOLD_AND_MONITOR";
        String rationale = replenishment > 0
                ? "Projected coverage is below the disruption-adjusted target stock level."
                : "Available inventory covers lead time plus disruption-adjusted safety buffer.";

        return new InventoryRecommendationDto(
                position.materialSku(),
                position.warehouseCode(),
                position.location(),
                position.availableQuantity(),
                position.averageDailyDemand(),
                BigDecimal.valueOf(coverageDays).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                bufferDays,
                RiskMath.quantity(targetQuantity),
                RiskMath.quantity(replenishment),
                recommendation,
                rationale
        );
    }

    private List<InventoryPositionDto> fallbackInventory(String materialType) {
        String sku = materialType == null || materialType.isBlank()
                ? "MAT-GENERIC"
                : "MAT-" + materialType.trim().toUpperCase().replaceAll("[^A-Z0-9]+", "-");
        return List.of(
                new InventoryPositionDto(sku, "PUN-WH-01", "Pune", BigDecimal.valueOf(18000),
                        BigDecimal.valueOf(1200), 14, 12),
                new InventoryPositionDto(sku, "MUM-PORT-02", "Mumbai Port", BigDecimal.valueOf(9000),
                        BigDecimal.valueOf(1200), 8, 8)
        );
    }
}

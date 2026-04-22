package com.akash.supplychain.agent.client;

import com.akash.supplychain.agent.dto.InventoryRecommendationDto;
import com.akash.supplychain.agent.dto.MitigationOption;
import com.akash.supplychain.agent.dto.MitigationPlan;
import com.akash.supplychain.agent.dto.MitigationPlanRequest;
import com.akash.supplychain.agent.dto.RouteLegDto;
import com.akash.supplychain.agent.dto.RouteRecommendationDto;
import com.akash.supplychain.agent.dto.SupplierScoreDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OptimizationServiceClient {

    private final RestClient restClient;

    public OptimizationServiceClient(@Value("${services.optimization-service.url:http://localhost:8084}") String optimizationServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(optimizationServiceUrl).build();
    }

    public MitigationPlan plan(MitigationPlanRequest request) {
        try {
            MitigationPlan response = restClient.post()
                    .uri("/optimization/mitigations/plan")
                    .body(request)
                    .retrieve()
                    .body(MitigationPlan.class);
            if (response != null) {
                return response;
            }
        } catch (RestClientException ignored) {
            // Fall through to local deterministic plan.
        }
        return fallbackPlan(request);
    }

    private MitigationPlan fallbackPlan(MitigationPlanRequest request) {
        List<SupplierScoreDto> suppliers = List.of(
                new SupplierScoreDto("ALT-IN-SEM-11", "Bengaluru Advanced Components", "India",
                        request.materialType(), BigDecimal.valueOf(89.40), 14, BigDecimal.valueOf(108.50),
                        BigDecimal.valueOf(8400), BigDecimal.valueOf(28), 84.10,
                        "PRIMARY_BACKUP", "Strong local lead time and manageable disruption risk."),
                new SupplierScoreDto("ALT-MY-SEM-14", "Penang Precision Manufacturing", "Malaysia",
                        request.materialType(), BigDecimal.valueOf(87.80), 18, BigDecimal.valueOf(101.20),
                        BigDecimal.valueOf(12500), BigDecimal.valueOf(34), 77.80,
                        "QUALIFY_NOW", "Good cost and capacity, but longer logistics chain.")
        );
        RouteRecommendationDto route = new RouteRecommendationDto(
                "AIR-EXPRESS",
                List.of(new RouteLegDto(request.origin(), request.destination(), "AIR", BigDecimal.valueOf(5100),
                        3, BigDecimal.valueOf(42000), BigDecimal.valueOf(18))),
                BigDecimal.valueOf(5100),
                3,
                BigDecimal.valueOf(42000),
                BigDecimal.valueOf(18),
                83.20,
                "USE_FOR_DISRUPTION_RESPONSE",
                "Fastest route with lower disruption exposure during the active event."
        );
        List<InventoryRecommendationDto> inventory = List.of(
                new InventoryRecommendationDto("MAT-" + request.materialType().toUpperCase(), "PUN-WH-01", "Pune",
                        BigDecimal.valueOf(18000), BigDecimal.valueOf(1200), 15.0, 19,
                        BigDecimal.valueOf(39600), BigDecimal.valueOf(21600),
                        "EXPEDITE_REPLENISHMENT", "Coverage is below the disruption-adjusted buffer.")
        );
        List<MitigationOption> options = List.of(
                new MitigationOption("SUPPLIER_SWITCH", "Qualify alternate supplier Bengaluru Advanced Components",
                        "Reserve capacity with ALT-IN-SEM-11 for emergency semiconductor supply.", 0.84,
                        BigDecimal.valueOf(54250), 7, "PROCUREMENT_MANAGER",
                        "Highest ranked alternate supplier under current disruption assumptions."),
                new MitigationOption("REROUTE", "Move priority freight to AIR-EXPRESS",
                        "Use air express for high-priority shipments until port risk normalizes.", 0.83,
                        BigDecimal.valueOf(42000), 3, "LOGISTICS_MANAGER",
                        "Fastest low-risk recovery path."),
                new MitigationOption("INVENTORY_BUFFER", "Increase buffer at PUN-WH-01",
                        "Replenish 21600 units for disrupted material coverage.", 0.86,
                        BigDecimal.valueOf(91800), 3, "INVENTORY_CONTROLLER",
                        "Stock coverage is below target buffer.")
        );
        return new MitigationPlan(
                request.supplyChainId(),
                request.disruptionTitle(),
                request.materialType(),
                request.severity(),
                request.exposureScore(),
                LocalDateTime.now(),
                suppliers,
                route,
                inventory,
                options,
                "Fallback optimization plan generated locally by agent-orchestrator."
        );
    }
}

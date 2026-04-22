package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MitigationPlan(
        Long supplyChainId,
        String disruptionTitle,
        String materialType,
        String severity,
        Double exposureScore,
        LocalDateTime generatedAt,
        List<SupplierScoreDto> rankedSuppliers,
        RouteRecommendationDto recommendedRoute,
        List<InventoryRecommendationDto> inventoryRecommendations,
        List<MitigationOption> mitigationOptions,
        String plannerSummary
) {
}

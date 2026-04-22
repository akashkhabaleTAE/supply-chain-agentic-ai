package com.akash.supplychain.optimization.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MitigationPlanResponse(
        Long supplyChainId,
        String disruptionTitle,
        String materialType,
        String severity,
        Double exposureScore,
        LocalDateTime generatedAt,
        List<SupplierScoreDto> rankedSuppliers,
        RouteRecommendationDto recommendedRoute,
        List<InventoryRecommendationDto> inventoryRecommendations,
        List<MitigationOptionDto> mitigationOptions
) {
}

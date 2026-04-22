package com.akash.supplychain.optimization.dto;

import java.util.List;

public record InventoryOptimizationResponse(
        String materialType,
        List<InventoryRecommendationDto> recommendations
) {
}

package com.akash.supplychain.optimization.dto;

import java.math.BigDecimal;

public record InventoryRecommendationDto(
        String materialSku,
        String warehouseCode,
        String location,
        BigDecimal availableQuantity,
        BigDecimal averageDailyDemand,
        double currentCoverageDays,
        int recommendedBufferDays,
        BigDecimal targetQuantity,
        BigDecimal replenishmentQuantity,
        String recommendation,
        String rationale
) {
}

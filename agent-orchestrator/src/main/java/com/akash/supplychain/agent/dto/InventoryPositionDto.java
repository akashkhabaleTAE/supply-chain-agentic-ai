package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;

public record InventoryPositionDto(
        String materialSku,
        String warehouseCode,
        String location,
        BigDecimal availableQuantity,
        BigDecimal averageDailyDemand,
        Integer leadTimeDays,
        Integer safetyStockDays
) {
}

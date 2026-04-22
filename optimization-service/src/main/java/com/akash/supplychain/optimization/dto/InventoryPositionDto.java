package com.akash.supplychain.optimization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record InventoryPositionDto(
        @NotBlank String materialSku,
        @NotBlank String warehouseCode,
        String location,
        @PositiveOrZero BigDecimal availableQuantity,
        @Positive BigDecimal averageDailyDemand,
        @Positive Integer leadTimeDays,
        @PositiveOrZero Integer safetyStockDays
) {
}

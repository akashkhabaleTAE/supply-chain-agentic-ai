package com.akash.supplychain.optimization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record InventoryOptimizationRequest(
        @NotBlank String materialType,
        String severity,
        Double exposureScore,
        List<@Valid InventoryPositionDto> inventoryPositions
) {
}

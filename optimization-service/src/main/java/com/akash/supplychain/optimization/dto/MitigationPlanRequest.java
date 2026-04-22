package com.akash.supplychain.optimization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MitigationPlanRequest(
        @NotNull Long supplyChainId,
        @NotBlank String disruptionTitle,
        @NotBlank String materialType,
        String origin,
        String destination,
        String severity,
        Double exposureScore,
        List<@Valid SupplierCandidateDto> candidateSuppliers,
        List<@Valid RouteOptionDto> routeOptions,
        List<@Valid InventoryPositionDto> inventoryPositions
) {
}

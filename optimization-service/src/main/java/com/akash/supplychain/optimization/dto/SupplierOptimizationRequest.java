package com.akash.supplychain.optimization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SupplierOptimizationRequest(
        @NotBlank String materialType,
        String severity,
        List<@Valid SupplierCandidateDto> candidateSuppliers
) {
}

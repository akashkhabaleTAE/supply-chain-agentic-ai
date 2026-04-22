package com.akash.supplychain.optimization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SupplierCandidateDto(
        @NotBlank String supplierCode,
        @NotBlank String name,
        String country,
        String region,
        @NotBlank String materialType,
        @PositiveOrZero BigDecimal reliabilityScore,
        @Positive Integer leadTimeDays,
        @PositiveOrZero BigDecimal unitCost,
        @PositiveOrZero BigDecimal availableCapacity,
        @PositiveOrZero BigDecimal riskScore
) {
}

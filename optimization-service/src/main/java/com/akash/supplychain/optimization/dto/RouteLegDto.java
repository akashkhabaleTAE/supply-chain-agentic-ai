package com.akash.supplychain.optimization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record RouteLegDto(
        @NotBlank String origin,
        @NotBlank String destination,
        @NotBlank String mode,
        @Positive BigDecimal distanceKm,
        @Positive Integer transitDays,
        @PositiveOrZero BigDecimal estimatedCost,
        @PositiveOrZero BigDecimal disruptionRisk
) {
}

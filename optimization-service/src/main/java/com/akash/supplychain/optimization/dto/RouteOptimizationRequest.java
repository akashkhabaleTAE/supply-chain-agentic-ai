package com.akash.supplychain.optimization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RouteOptimizationRequest(
        @NotBlank String origin,
        @NotBlank String destination,
        String severity,
        Double exposureScore,
        List<@Valid RouteOptionDto> routeOptions
) {
}

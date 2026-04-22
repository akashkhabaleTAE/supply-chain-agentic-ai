package com.akash.supplychain.optimization.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RouteOptionDto(
        @NotBlank String routeId,
        @NotEmpty List<@Valid RouteLegDto> legs
) {
}

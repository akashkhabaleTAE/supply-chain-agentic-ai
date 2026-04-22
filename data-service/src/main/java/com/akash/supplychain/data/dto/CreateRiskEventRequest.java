package com.akash.supplychain.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record CreateRiskEventRequest(
        @NotNull Long supplyChainId,
        @NotBlank String title,
        @NotBlank String description,
        String location,
        String type,
        String severity,
        @PositiveOrZero Double exposureScore,
        String source,
        LocalDateTime detectedAt
) {
}

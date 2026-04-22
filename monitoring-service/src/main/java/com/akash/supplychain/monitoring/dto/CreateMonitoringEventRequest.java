package com.akash.supplychain.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateMonitoringEventRequest(
        @NotNull Long supplyChainId,
        @JsonAlias("headline") @NotBlank String title,
        @JsonAlias("event") @NotBlank String description,
        String location,
        String eventType,
        String severity,
        String materialType,
        String source,
        LocalDateTime publishedAt
) {
}

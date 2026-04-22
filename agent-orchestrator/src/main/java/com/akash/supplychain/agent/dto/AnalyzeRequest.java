package com.akash.supplychain.agent.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AnalyzeRequest(
        @JsonAlias("event") @NotBlank String eventDescription,
        @NotNull Long supplyChainId,
        LocalDateTime timestamp,
        String source
) {
    public LocalDateTime effectiveTimestamp() {
        return timestamp == null ? LocalDateTime.now() : timestamp;
    }

    public String effectiveSource() {
        return source == null || source.isBlank() ? "manual-api" : source;
    }
}

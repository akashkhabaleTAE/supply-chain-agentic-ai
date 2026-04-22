package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;

public record RiskEventDto(
        Long id,
        Long supplyChainId,
        String title,
        String description,
        String location,
        String type,
        String severity,
        String status,
        Double exposureScore,
        String source,
        LocalDateTime detectedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

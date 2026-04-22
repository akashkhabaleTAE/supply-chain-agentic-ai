package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;

public record CreateRiskEventRequest(
        Long supplyChainId,
        String title,
        String description,
        String location,
        String type,
        String severity,
        Double exposureScore,
        String source,
        LocalDateTime detectedAt
) {
}

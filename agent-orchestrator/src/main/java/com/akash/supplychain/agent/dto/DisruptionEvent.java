package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DisruptionEvent(
        String title,
        String description,
        String eventType,
        String severity,
        String location,
        String materialType,
        double confidenceScore,
        List<String> detectedSignals,
        LocalDateTime detectedAt
) {
}

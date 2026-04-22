package com.akash.supplychain.monitoring.dto;

import java.time.LocalDateTime;

public record MonitoringEventDto(
        Long id,
        Long supplyChainId,
        String title,
        String description,
        String location,
        String eventType,
        String severity,
        String materialType,
        String source,
        MonitoringEventStatus status,
        LocalDateTime publishedAt,
        LocalDateTime detectedAt,
        LocalDateTime analyzedAt,
        String normalizedSummary
) {
}

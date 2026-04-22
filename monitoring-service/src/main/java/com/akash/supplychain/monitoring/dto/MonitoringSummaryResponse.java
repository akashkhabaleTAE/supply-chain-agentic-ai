package com.akash.supplychain.monitoring.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record MonitoringSummaryResponse(
        long totalEvents,
        long newEvents,
        long normalizedEvents,
        long analyzedEvents,
        Map<String, Long> eventsBySeverity,
        LocalDateTime generatedAt
) {
}

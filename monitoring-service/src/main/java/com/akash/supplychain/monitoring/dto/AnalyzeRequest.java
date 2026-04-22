package com.akash.supplychain.monitoring.dto;

import java.time.LocalDateTime;

public record AnalyzeRequest(
        String event,
        Long supplyChainId,
        LocalDateTime timestamp,
        String source
) {
}

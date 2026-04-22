package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;

public record NewsEvent(
        String headline,
        String description,
        String source,
        Long supplyChainId,
        LocalDateTime publishedAt
) {
}

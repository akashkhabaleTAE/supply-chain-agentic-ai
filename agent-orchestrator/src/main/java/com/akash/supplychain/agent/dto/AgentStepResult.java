package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;

public record AgentStepResult(
        String agentName,
        String status,
        String summary,
        long durationMs,
        LocalDateTime completedAt
) {
}

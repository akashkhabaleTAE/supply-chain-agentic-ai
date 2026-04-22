package com.akash.supplychain.agent.dto;

import java.util.List;

public record AnalyzeResponse(
        String workflowId,
        RiskReport report,
        List<AgentStepResult> agentTrace
) {
}

package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RiskReport(
        Long supplyChainId,
        String disruptionTitle,
        String riskLevel,
        double exposureScore,
        List<MitigationOption> options,
        List<SourcingAction> sourcingActions,
        List<TraceNode> traceNodes,
        String executiveSummary,
        LocalDateTime generatedAt
) {
}

package com.akash.supplychain.agent.dto;

import java.util.List;

public record TraceResult(
        RiskAnalysis riskAnalysis,
        List<TraceNode> traceNodes,
        String traceSummary
) {
}

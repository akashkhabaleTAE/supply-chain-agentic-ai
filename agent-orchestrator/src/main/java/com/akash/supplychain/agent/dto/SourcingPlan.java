package com.akash.supplychain.agent.dto;

import java.util.List;

public record SourcingPlan(
        MitigationPlan mitigationPlan,
        List<SourcingAction> sourcingActions,
        String executionSummary
) {
}

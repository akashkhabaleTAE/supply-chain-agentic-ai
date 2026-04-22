package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;

public record SourcingAction(
        String actionId,
        String actionType,
        String target,
        String status,
        BigDecimal estimatedCost,
        String approvalRequired,
        String notes
) {
}

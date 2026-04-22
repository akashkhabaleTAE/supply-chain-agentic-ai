package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;

public record MitigationOption(
        String optionType,
        String title,
        String action,
        double confidenceScore,
        BigDecimal estimatedCostImpact,
        int estimatedRecoveryDays,
        String approvalRequired,
        String rationale
) {
}

package com.akash.supplychain.optimization.dto;

import java.math.BigDecimal;

public record MitigationOptionDto(
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

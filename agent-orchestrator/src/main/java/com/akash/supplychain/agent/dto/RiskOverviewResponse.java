package com.akash.supplychain.agent.dto;

import java.util.List;
import java.util.Map;

public record RiskOverviewResponse(
        Long supplyChainId,
        double maxExposureScore,
        long openRiskEvents,
        long criticalOrHighSuppliers,
        Map<String, Long> riskEventsBySeverity,
        List<RiskEventDto> latestEvents,
        List<SupplierDto> highRiskSuppliers
) {
}

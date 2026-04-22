package com.akash.supplychain.data.dto;

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

package com.akash.supplychain.agent.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record DashboardData(
        Long supplyChainId,
        double maxExposureScore,
        long openRiskEvents,
        long criticalOrHighSuppliers,
        Map<String, Long> riskEventsBySeverity,
        List<RiskEventDto> latestEvents,
        List<SupplierDto> highRiskSuppliers,
        List<String> heatmapLabels,
        List<Double> heatmapScores,
        LocalDateTime generatedAt
) {
}

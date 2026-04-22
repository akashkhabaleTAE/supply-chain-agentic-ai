package com.akash.supplychain.agent.dto;

import java.util.List;

public record RiskAnalysis(
        DisruptionEvent disruptionEvent,
        List<SupplierImpact> impactedSuppliers,
        List<MaterialExposure> materialExposures,
        double exposureScore,
        String riskLevel,
        String analysisSummary
) {
}

package com.akash.supplychain.optimization.dto;

import java.math.BigDecimal;

public record SupplierScoreDto(
        String supplierCode,
        String name,
        String country,
        String materialType,
        BigDecimal reliabilityScore,
        Integer leadTimeDays,
        BigDecimal unitCost,
        BigDecimal availableCapacity,
        BigDecimal riskScore,
        double optimizationScore,
        String recommendation,
        String rationale
) {
}

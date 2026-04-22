package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;

public record SupplierCandidateDto(
        String supplierCode,
        String name,
        String country,
        String region,
        String materialType,
        BigDecimal reliabilityScore,
        Integer leadTimeDays,
        BigDecimal unitCost,
        BigDecimal availableCapacity,
        BigDecimal riskScore
) {
}

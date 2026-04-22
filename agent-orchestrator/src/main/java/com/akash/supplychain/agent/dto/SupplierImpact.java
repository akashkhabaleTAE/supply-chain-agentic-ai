package com.akash.supplychain.agent.dto;

public record SupplierImpact(
        String supplierCode,
        String supplierName,
        Integer tier,
        String country,
        String materialType,
        String baselineRisk,
        double impactScore,
        String impactReason
) {
}

package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SupplierDto(
        Long id,
        Long supplyChainId,
        String supplierCode,
        String name,
        Integer tier,
        String country,
        String region,
        String materialType,
        String baselineRisk,
        BigDecimal reliabilityScore,
        Integer leadTimeDays,
        boolean active,
        String dependencyGraphJson,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

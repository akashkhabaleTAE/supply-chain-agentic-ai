package com.akash.supplychain.data.dto;

import com.akash.supplychain.data.entities.Supplier;

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
    public static SupplierDto fromEntity(Supplier supplier) {
        return new SupplierDto(
                supplier.getId(),
                supplier.getSupplyChainId(),
                supplier.getSupplierCode(),
                supplier.getName(),
                supplier.getTier(),
                supplier.getCountry(),
                supplier.getRegion(),
                supplier.getMaterialType(),
                supplier.getBaselineRisk().name(),
                supplier.getReliabilityScore(),
                supplier.getLeadTimeDays(),
                supplier.isActive(),
                supplier.getDependencyGraphJson(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }
}

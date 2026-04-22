package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;

public record MaterialExposure(
        String materialSku,
        String materialName,
        String supplierCode,
        String warehouseCode,
        BigDecimal netAvailableQuantity,
        BigDecimal reorderPoint,
        double coverageRisk,
        String rationale
) {
}

package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryDto(
        Long id,
        Long supplyChainId,
        Long materialId,
        String materialSku,
        String materialName,
        Long supplierId,
        String supplierCode,
        String supplierName,
        String warehouseCode,
        String location,
        BigDecimal availableQuantity,
        BigDecimal reservedQuantity,
        BigDecimal reorderPoint,
        BigDecimal netAvailableQuantity,
        boolean belowReorderPoint,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

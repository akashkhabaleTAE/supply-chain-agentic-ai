package com.akash.supplychain.data.dto;

import com.akash.supplychain.data.entities.Inventory;

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
    public static InventoryDto fromEntity(Inventory inventory) {
        BigDecimal netAvailable = inventory.getAvailableQuantity().subtract(inventory.getReservedQuantity());
        return new InventoryDto(
                inventory.getId(),
                inventory.getSupplyChainId(),
                inventory.getMaterial().getId(),
                inventory.getMaterial().getSku(),
                inventory.getMaterial().getName(),
                inventory.getSupplier().getId(),
                inventory.getSupplier().getSupplierCode(),
                inventory.getSupplier().getName(),
                inventory.getWarehouseCode(),
                inventory.getLocation(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getReorderPoint(),
                netAvailable,
                netAvailable.compareTo(inventory.getReorderPoint()) < 0,
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}

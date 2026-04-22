package com.akash.supplychain.data.dto;

import com.akash.supplychain.data.entities.Material;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MaterialDto(
        Long id,
        String sku,
        String name,
        String type,
        String unitOfMeasure,
        String criticality,
        BigDecimal averageDailyDemand,
        Integer safetyStockDays,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MaterialDto fromEntity(Material material) {
        return new MaterialDto(
                material.getId(),
                material.getSku(),
                material.getName(),
                material.getType(),
                material.getUnitOfMeasure(),
                material.getCriticality().name(),
                material.getAverageDailyDemand(),
                material.getSafetyStockDays(),
                material.getCreatedAt(),
                material.getUpdatedAt()
        );
    }
}

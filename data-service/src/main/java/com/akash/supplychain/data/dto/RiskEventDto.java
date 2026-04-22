package com.akash.supplychain.data.dto;

import com.akash.supplychain.data.entities.RiskEvent;

import java.time.LocalDateTime;

public record RiskEventDto(
        Long id,
        Long supplyChainId,
        String title,
        String description,
        String location,
        String type,
        String severity,
        String status,
        Double exposureScore,
        String source,
        LocalDateTime detectedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RiskEventDto fromEntity(RiskEvent riskEvent) {
        return new RiskEventDto(
                riskEvent.getId(),
                riskEvent.getSupplyChainId(),
                riskEvent.getTitle(),
                riskEvent.getDescription(),
                riskEvent.getLocation(),
                riskEvent.getType().name(),
                riskEvent.getSeverity().name(),
                riskEvent.getStatus().name(),
                riskEvent.getExposureScore(),
                riskEvent.getSource(),
                riskEvent.getDetectedAt(),
                riskEvent.getCreatedAt(),
                riskEvent.getUpdatedAt()
        );
    }
}

package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;

public record RouteLegDto(
        String origin,
        String destination,
        String mode,
        BigDecimal distanceKm,
        Integer transitDays,
        BigDecimal estimatedCost,
        BigDecimal disruptionRisk
) {
}

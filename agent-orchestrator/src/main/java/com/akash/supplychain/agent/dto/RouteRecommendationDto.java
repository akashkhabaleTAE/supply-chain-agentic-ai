package com.akash.supplychain.agent.dto;

import java.math.BigDecimal;
import java.util.List;

public record RouteRecommendationDto(
        String routeId,
        List<RouteLegDto> legs,
        BigDecimal totalDistanceKm,
        Integer totalTransitDays,
        BigDecimal totalEstimatedCost,
        BigDecimal averageDisruptionRisk,
        double optimizationScore,
        String recommendation,
        String rationale
) {
}

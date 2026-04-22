package com.akash.supplychain.optimization.dto;

import java.util.List;

public record RouteOptimizationResponse(
        String origin,
        String destination,
        RouteRecommendationDto recommendedRoute,
        List<RouteRecommendationDto> rankedRoutes
) {
}

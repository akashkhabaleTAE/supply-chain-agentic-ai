package com.akash.supplychain.optimization.solver;

import com.akash.supplychain.optimization.dto.RouteLegDto;
import com.akash.supplychain.optimization.dto.RouteOptimizationRequest;
import com.akash.supplychain.optimization.dto.RouteOptimizationResponse;
import com.akash.supplychain.optimization.dto.RouteOptionDto;
import com.akash.supplychain.optimization.dto.RouteRecommendationDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class RouteOptimizerService {

    public RouteOptimizationResponse optimize(RouteOptimizationRequest request) {
        List<RouteOptionDto> options = request.routeOptions() == null || request.routeOptions().isEmpty()
                ? fallbackRoutes(request.origin(), request.destination())
                : request.routeOptions();

        List<RouteMetrics> metrics = options.stream().map(this::toMetrics).toList();
        double minCost = metrics.stream().mapToDouble(RouteMetrics::totalCost).min().orElse(1);
        double maxCost = metrics.stream().mapToDouble(RouteMetrics::totalCost).max().orElse(1);
        double minDays = metrics.stream().mapToDouble(RouteMetrics::totalDays).min().orElse(1);
        double maxDays = metrics.stream().mapToDouble(RouteMetrics::totalDays).max().orElse(1);
        double minRisk = metrics.stream().mapToDouble(RouteMetrics::averageRisk).min().orElse(1);
        double maxRisk = metrics.stream().mapToDouble(RouteMetrics::averageRisk).max().orElse(1);
        double severityMultiplier = RiskMath.severityMultiplier(request.severity());

        List<RouteRecommendationDto> ranked = metrics.stream()
                .map(metric -> scoreRoute(metric, minCost, maxCost, minDays, maxDays, minRisk, maxRisk, severityMultiplier))
                .sorted(Comparator.comparingDouble(RouteRecommendationDto::optimizationScore).reversed())
                .toList();

        return new RouteOptimizationResponse(
                request.origin(),
                request.destination(),
                ranked.getFirst(),
                ranked
        );
    }

    private RouteRecommendationDto scoreRoute(RouteMetrics metric, double minCost, double maxCost, double minDays,
                                              double maxDays, double minRisk, double maxRisk, double severityMultiplier) {
        double costScore = RiskMath.normalizeInverse(metric.totalCost(), minCost, maxCost);
        double timeScore = RiskMath.normalizeInverse(metric.totalDays(), minDays, maxDays);
        double riskScore = RiskMath.normalizeInverse(metric.averageRisk() * severityMultiplier, minRisk, maxRisk * severityMultiplier);
        double score = timeScore * 0.35 + riskScore * 0.40 + costScore * 0.25;

        String recommendation = score >= 75 ? "USE_FOR_DISRUPTION_RESPONSE" : score >= 55 ? "KEEP_AS_BACKUP" : "AVOID_UNLESS_REQUIRED";
        String rationale = "Route score prioritizes lower disruption risk, faster transit, and controlled logistics cost.";

        return new RouteRecommendationDto(
                metric.option().routeId(),
                metric.option().legs(),
                RiskMath.quantity(metric.totalDistance()),
                metric.totalDays(),
                RiskMath.money(metric.totalCost()),
                BigDecimal.valueOf(metric.averageRisk()).setScale(2, RoundingMode.HALF_UP),
                Math.round(score * 100.0) / 100.0,
                recommendation,
                rationale
        );
    }

    private RouteMetrics toMetrics(RouteOptionDto option) {
        double distance = option.legs().stream().mapToDouble(leg -> leg.distanceKm().doubleValue()).sum();
        int days = option.legs().stream().mapToInt(RouteLegDto::transitDays).sum();
        double cost = option.legs().stream().mapToDouble(leg -> safeDecimal(leg.estimatedCost(), 0)).sum();
        double risk = option.legs().stream().mapToDouble(leg -> safeDecimal(leg.disruptionRisk(), 0)).average().orElse(0);
        return new RouteMetrics(option, distance, days, cost, risk);
    }

    private double safeDecimal(BigDecimal value, double fallback) {
        return value == null ? fallback : value.doubleValue();
    }

    private List<RouteOptionDto> fallbackRoutes(String origin, String destination) {
        return List.of(
                new RouteOptionDto("AIR-EXPRESS", List.of(
                        new RouteLegDto(origin, destination, "AIR", BigDecimal.valueOf(5100), 3,
                                BigDecimal.valueOf(42000), BigDecimal.valueOf(18))
                )),
                new RouteOptionDto("SEA-MUMBAI-TRUCK", List.of(
                        new RouteLegDto(origin, "Mumbai Port", "SEA", BigDecimal.valueOf(6200), 11,
                                BigDecimal.valueOf(14000), BigDecimal.valueOf(38)),
                        new RouteLegDto("Mumbai Port", destination, "TRUCK", BigDecimal.valueOf(150), 1,
                                BigDecimal.valueOf(1800), BigDecimal.valueOf(12))
                )),
                new RouteOptionDto("SEA-CHENNAI-RAIL", List.of(
                        new RouteLegDto(origin, "Chennai Port", "SEA", BigDecimal.valueOf(5900), 10,
                                BigDecimal.valueOf(13200), BigDecimal.valueOf(30)),
                        new RouteLegDto("Chennai Port", destination, "RAIL", BigDecimal.valueOf(1180), 3,
                                BigDecimal.valueOf(2500), BigDecimal.valueOf(16))
                ))
        );
    }

    private record RouteMetrics(RouteOptionDto option, double totalDistance, int totalDays, double totalCost,
                                double averageRisk) {
    }
}

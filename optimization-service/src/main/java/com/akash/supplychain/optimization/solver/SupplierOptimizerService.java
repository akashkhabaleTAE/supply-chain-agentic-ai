package com.akash.supplychain.optimization.solver;

import com.akash.supplychain.optimization.dto.SupplierCandidateDto;
import com.akash.supplychain.optimization.dto.SupplierScoreDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class SupplierOptimizerService {

    public List<SupplierScoreDto> rankSuppliers(String materialType, String severity,
                                                List<SupplierCandidateDto> candidateSuppliers) {
        List<SupplierCandidateDto> suppliers = candidateSuppliers == null || candidateSuppliers.isEmpty()
                ? fallbackSuppliers(materialType)
                : candidateSuppliers;

        double maxLeadTime = suppliers.stream().mapToDouble(s -> safeInteger(s.leadTimeDays(), 21)).max().orElse(21);
        double minLeadTime = suppliers.stream().mapToDouble(s -> safeInteger(s.leadTimeDays(), 21)).min().orElse(7);
        double maxCost = suppliers.stream().mapToDouble(s -> safeDecimal(s.unitCost(), 100)).max().orElse(100);
        double minCost = suppliers.stream().mapToDouble(s -> safeDecimal(s.unitCost(), 100)).min().orElse(50);
        double severityMultiplier = RiskMath.severityMultiplier(severity);

        return suppliers.stream()
                .map(supplier -> scoreSupplier(supplier, minLeadTime, maxLeadTime, minCost, maxCost, severityMultiplier))
                .sorted(Comparator.comparingDouble(SupplierScoreDto::optimizationScore).reversed())
                .toList();
    }

    private SupplierScoreDto scoreSupplier(SupplierCandidateDto supplier, double minLeadTime, double maxLeadTime,
                                           double minCost, double maxCost, double severityMultiplier) {
        double reliability = RiskMath.clamp(safeDecimal(supplier.reliabilityScore(), 85), 0, 100);
        double riskScore = RiskMath.clamp(safeDecimal(supplier.riskScore(), 35) * severityMultiplier, 0, 100);
        double riskInverse = 100.0 - riskScore;
        double leadTimeScore = RiskMath.normalizeInverse(safeInteger(supplier.leadTimeDays(), 21), minLeadTime, maxLeadTime);
        double costScore = RiskMath.normalizeInverse(safeDecimal(supplier.unitCost(), 100), minCost, maxCost);
        double capacityScore = Math.min(100.0, safeDecimal(supplier.availableCapacity(), 10_000) / 200.0);

        double score = reliability * 0.35
                + riskInverse * 0.25
                + leadTimeScore * 0.20
                + capacityScore * 0.10
                + costScore * 0.10;

        String recommendation = score >= 82 ? "PRIMARY_BACKUP" : score >= 68 ? "QUALIFY_NOW" : "MONITOR_ONLY";
        String rationale = "Score balances reliability, disruption risk, lead time, capacity, and cost.";

        return new SupplierScoreDto(
                supplier.supplierCode(),
                supplier.name(),
                supplier.country(),
                supplier.materialType(),
                defaultDecimal(supplier.reliabilityScore(), 85),
                supplier.leadTimeDays() == null ? 21 : supplier.leadTimeDays(),
                defaultDecimal(supplier.unitCost(), 100),
                defaultDecimal(supplier.availableCapacity(), 10_000),
                defaultDecimal(supplier.riskScore(), 35),
                Math.round(score * 100.0) / 100.0,
                recommendation,
                rationale
        );
    }

    private List<SupplierCandidateDto> fallbackSuppliers(String materialType) {
        String normalizedMaterial = materialType == null || materialType.isBlank() ? "Semiconductor" : materialType;
        return List.of(
                new SupplierCandidateDto("ALT-IN-SEM-11", "Bengaluru Advanced Components", "India", "Karnataka",
                        normalizedMaterial, BigDecimal.valueOf(89.4), 14, BigDecimal.valueOf(108.50),
                        BigDecimal.valueOf(8400), BigDecimal.valueOf(28)),
                new SupplierCandidateDto("ALT-MY-SEM-14", "Penang Precision Manufacturing", "Malaysia", "Penang",
                        normalizedMaterial, BigDecimal.valueOf(87.8), 18, BigDecimal.valueOf(101.20),
                        BigDecimal.valueOf(12500), BigDecimal.valueOf(34)),
                new SupplierCandidateDto("ALT-VN-SEM-08", "Da Nang Electronics Hub", "Vietnam", "Da Nang",
                        normalizedMaterial, BigDecimal.valueOf(82.7), 16, BigDecimal.valueOf(96.70),
                        BigDecimal.valueOf(6600), BigDecimal.valueOf(42))
        );
    }

    private double safeDecimal(BigDecimal value, double fallback) {
        return value == null ? fallback : value.doubleValue();
    }

    private BigDecimal defaultDecimal(BigDecimal value, double fallback) {
        return value == null ? BigDecimal.valueOf(fallback) : value;
    }

    private int safeInteger(Integer value, int fallback) {
        return value == null ? fallback : value;
    }
}

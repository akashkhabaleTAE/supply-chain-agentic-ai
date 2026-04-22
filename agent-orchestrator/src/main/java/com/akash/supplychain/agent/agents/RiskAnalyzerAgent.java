package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.dto.InventoryDto;
import com.akash.supplychain.agent.dto.MaterialExposure;
import com.akash.supplychain.agent.dto.RiskAnalysis;
import com.akash.supplychain.agent.dto.RiskAssessment;
import com.akash.supplychain.agent.dto.SupplierImpact;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RiskAnalyzerAgent {

    public RiskAnalysis analyze(RiskAssessment assessment) {
        List<MaterialExposure> exposures = assessment.inventoryPositions().stream()
                .filter(inventory -> isRelevantInventory(assessment, inventory))
                .map(this::toMaterialExposure)
                .toList();

        double supplierRisk = assessment.impactedSuppliers().stream()
                .mapToDouble(SupplierImpact::impactScore)
                .average()
                .orElse(severityBase(assessment.disruptionEvent().severity()));
        double materialRisk = exposures.stream()
                .mapToDouble(MaterialExposure::coverageRisk)
                .average()
                .orElse(45.0);
        double signalBoost = assessment.disruptionEvent().detectedSignals().size() * 3.0;
        double exposureScore = clamp(supplierRisk * 0.55 + materialRisk * 0.35 + signalBoost, 0, 100);
        String riskLevel = riskLevel(exposureScore);
        String summary = "%s exposure calculated at %.1f based on supplier impact, material coverage, and event severity."
                .formatted(riskLevel, exposureScore);

        return new RiskAnalysis(
                assessment.disruptionEvent(),
                assessment.impactedSuppliers(),
                exposures,
                Math.round(exposureScore * 10.0) / 10.0,
                riskLevel,
                summary
        );
    }

    private boolean isRelevantInventory(RiskAssessment assessment, InventoryDto inventory) {
        return assessment.impactedSuppliers().stream()
                .anyMatch(impact -> impact.supplierCode().equalsIgnoreCase(inventory.supplierCode()))
                || containsIgnoreCase(inventory.materialName(), assessment.disruptionEvent().materialType())
                || containsIgnoreCase(inventory.materialSku(), assessment.disruptionEvent().materialType());
    }

    private MaterialExposure toMaterialExposure(InventoryDto inventory) {
        BigDecimal net = inventory.netAvailableQuantity() == null ? BigDecimal.ZERO : inventory.netAvailableQuantity();
        BigDecimal reorder = inventory.reorderPoint() == null ? BigDecimal.ONE : inventory.reorderPoint();
        double ratio = reorder.signum() == 0 ? 1.0 : net.doubleValue() / reorder.doubleValue();
        double coverageRisk = inventory.belowReorderPoint() ? 75.0 : clamp(100.0 - ratio * 70.0, 5.0, 65.0);
        String rationale = inventory.belowReorderPoint()
                ? "Net available stock is below reorder point."
                : "Inventory is above reorder point but still exposed to replenishment delay.";
        return new MaterialExposure(
                inventory.materialSku(),
                inventory.materialName(),
                inventory.supplierCode(),
                inventory.warehouseCode(),
                net,
                reorder,
                Math.round(coverageRisk * 10.0) / 10.0,
                rationale
        );
    }

    private double severityBase(String severity) {
        return switch (severity == null ? "" : severity.toUpperCase()) {
            case "CRITICAL" -> 85.0;
            case "HIGH" -> 70.0;
            case "MEDIUM" -> 45.0;
            default -> 25.0;
        };
    }

    private String riskLevel(double score) {
        if (score >= 85) {
            return "CRITICAL";
        }
        if (score >= 65) {
            return "HIGH";
        }
        if (score >= 40) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private boolean containsIgnoreCase(String value, String token) {
        return value != null && token != null && value.toLowerCase().contains(token.toLowerCase());
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

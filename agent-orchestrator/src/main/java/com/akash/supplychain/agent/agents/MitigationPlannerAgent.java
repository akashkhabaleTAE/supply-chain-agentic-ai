package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.client.OptimizationServiceClient;
import com.akash.supplychain.agent.dto.InventoryPositionDto;
import com.akash.supplychain.agent.dto.MitigationPlan;
import com.akash.supplychain.agent.dto.MitigationPlanRequest;
import com.akash.supplychain.agent.dto.SupplierCandidateDto;
import com.akash.supplychain.agent.dto.SupplierImpact;
import com.akash.supplychain.agent.dto.TraceResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MitigationPlannerAgent {

    private final OptimizationServiceClient optimizationServiceClient;

    public MitigationPlannerAgent(OptimizationServiceClient optimizationServiceClient) {
        this.optimizationServiceClient = optimizationServiceClient;
    }

    public MitigationPlan plan(Long supplyChainId, TraceResult traceResult) {
        var analysis = traceResult.riskAnalysis();
        MitigationPlanRequest request = new MitigationPlanRequest(
                supplyChainId,
                analysis.disruptionEvent().title(),
                analysis.disruptionEvent().materialType(),
                analysis.disruptionEvent().location(),
                "Pune",
                analysis.riskLevel(),
                analysis.exposureScore(),
                toCandidates(analysis.impactedSuppliers()),
                null,
                toInventoryPositions(traceResult)
        );
        MitigationPlan plan = optimizationServiceClient.plan(request);
        return new MitigationPlan(
                plan.supplyChainId(),
                plan.disruptionTitle(),
                plan.materialType(),
                plan.severity(),
                plan.exposureScore(),
                plan.generatedAt(),
                plan.rankedSuppliers(),
                plan.recommendedRoute(),
                plan.inventoryRecommendations(),
                plan.mitigationOptions(),
                "Mitigation plan created with %d options and %d ranked supplier(s)."
                        .formatted(plan.mitigationOptions().size(), plan.rankedSuppliers().size())
        );
    }

    private List<SupplierCandidateDto> toCandidates(List<SupplierImpact> impacts) {
        if (impacts.isEmpty()) {
            return null;
        }
        return impacts.stream()
                .map(impact -> new SupplierCandidateDto(
                        "ALT-" + impact.supplierCode(),
                        "Alternate for " + impact.supplierName(),
                        impact.country(),
                        impact.country(),
                        impact.materialType(),
                        BigDecimal.valueOf(Math.max(78.0, 100.0 - impact.impactScore() / 4.0)),
                        Math.max(7, 28 - impact.tier() * 3),
                        BigDecimal.valueOf(100 + impact.impactScore() / 3.0),
                        BigDecimal.valueOf(8000),
                        BigDecimal.valueOf(Math.max(20.0, impact.impactScore() / 2.5))
                ))
                .toList();
    }

    private List<InventoryPositionDto> toInventoryPositions(TraceResult traceResult) {
        if (traceResult.riskAnalysis().materialExposures().isEmpty()) {
            return null;
        }
        return traceResult.riskAnalysis().materialExposures().stream()
                .map(exposure -> new InventoryPositionDto(
                        exposure.materialSku(),
                        exposure.warehouseCode(),
                        "Pune",
                        exposure.netAvailableQuantity(),
                        BigDecimal.valueOf(1200),
                        14,
                        12
                ))
                .toList();
    }
}

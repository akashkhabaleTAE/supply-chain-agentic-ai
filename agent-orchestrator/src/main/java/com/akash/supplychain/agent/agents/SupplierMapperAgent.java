package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.dto.DisruptionEvent;
import com.akash.supplychain.agent.dto.InventoryDto;
import com.akash.supplychain.agent.dto.RiskAssessment;
import com.akash.supplychain.agent.dto.SupplierDto;
import com.akash.supplychain.agent.dto.SupplierImpact;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SupplierMapperAgent {

    public RiskAssessment map(DisruptionEvent event, List<SupplierDto> suppliers, List<InventoryDto> inventory) {
        List<SupplierImpact> impacts = suppliers.stream()
                .map(supplier -> toImpact(event, supplier))
                .filter(impact -> impact.impactScore() >= 35.0)
                .sorted(Comparator.comparingDouble(SupplierImpact::impactScore).reversed())
                .toList();

        String summary = impacts.isEmpty()
                ? "No direct supplier match found; workflow will use material-level risk assumptions."
                : "Mapped disruption to %d supplier(s), led by %s.".formatted(impacts.size(), impacts.getFirst().supplierName());

        return new RiskAssessment(event, impacts, suppliers, inventory, summary);
    }

    private SupplierImpact toImpact(DisruptionEvent event, SupplierDto supplier) {
        double score = 0.0;
        String reason = "Indirect exposure";

        if (equalsIgnoreCase(event.materialType(), supplier.materialType())) {
            score += 35.0;
            reason = "Material type match";
        }
        if (containsIgnoreCase(supplier.country(), event.location()) || containsIgnoreCase(supplier.region(), event.location())) {
            score += 35.0;
            reason = reason + " and regional match";
        }
        if ("HIGH".equalsIgnoreCase(supplier.baselineRisk())) {
            score += 15.0;
        }
        if ("CRITICAL".equalsIgnoreCase(event.severity())) {
            score += 15.0;
        } else if ("HIGH".equalsIgnoreCase(event.severity())) {
            score += 10.0;
        }
        if (supplier.tier() != null && supplier.tier() == 1) {
            score += 5.0;
        }

        return new SupplierImpact(
                supplier.supplierCode(),
                supplier.name(),
                supplier.tier(),
                supplier.country(),
                supplier.materialType(),
                supplier.baselineRisk(),
                Math.min(100.0, score),
                reason
        );
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return left != null && right != null && left.equalsIgnoreCase(right);
    }

    private boolean containsIgnoreCase(String left, String right) {
        return left != null && right != null && left.toLowerCase().contains(right.toLowerCase());
    }
}

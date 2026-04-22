package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.dto.RiskAnalysis;
import com.akash.supplychain.agent.dto.SupplierImpact;
import com.akash.supplychain.agent.dto.TraceNode;
import com.akash.supplychain.agent.dto.TraceResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TracerAgent {

    public TraceResult trace(RiskAnalysis analysis) {
        List<TraceNode> nodes = new ArrayList<>();
        for (SupplierImpact impact : analysis.impactedSuppliers()) {
            nodes.add(new TraceNode(
                    impact.supplierCode(),
                    "SUPPLIER",
                    impact.supplierName(),
                    impact.tier(),
                    Math.round((impact.impactScore() * 0.90) * 10.0) / 10.0,
                    List.of("Pune Assembly Hub", impact.materialType())
            ));
        }
        nodes.add(new TraceNode(
                "Pune Assembly Hub",
                "PLANT",
                "Pune Assembly Hub",
                0,
                Math.round((analysis.exposureScore() * 0.80) * 10.0) / 10.0,
                analysis.impactedSuppliers().stream().map(SupplierImpact::supplierCode).toList()
        ));
        nodes.add(new TraceNode(
                analysis.disruptionEvent().materialType(),
                "MATERIAL",
                analysis.disruptionEvent().materialType(),
                null,
                analysis.exposureScore(),
                analysis.materialExposures().stream().map(exposure -> exposure.warehouseCode()).toList()
        ));

        String summary = "Risk propagated across %d supplier/material/plant node(s).".formatted(nodes.size());
        return new TraceResult(analysis, nodes, summary);
    }
}

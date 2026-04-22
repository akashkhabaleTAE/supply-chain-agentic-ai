package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.dto.RiskReport;
import com.akash.supplychain.agent.dto.SourcingPlan;
import com.akash.supplychain.agent.dto.TraceResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReporterAgent {

    public RiskReport report(Long supplyChainId, TraceResult traceResult, SourcingPlan sourcingPlan) {
        var analysis = traceResult.riskAnalysis();
        String summary = """
                %s disruption affecting %s has %s exposure score %.1f. The workflow mapped %d impacted supplier(s), traced %d supply-chain node(s), and prepared %d mitigation option(s). Recommended next action: %s.
                """.formatted(
                analysis.disruptionEvent().eventType(),
                analysis.disruptionEvent().materialType(),
                analysis.riskLevel(),
                analysis.exposureScore(),
                analysis.impactedSuppliers().size(),
                traceResult.traceNodes().size(),
                sourcingPlan.mitigationPlan().mitigationOptions().size(),
                sourcingPlan.mitigationPlan().mitigationOptions().getFirst().title()
        ).trim();

        return new RiskReport(
                supplyChainId,
                analysis.disruptionEvent().title(),
                analysis.riskLevel(),
                analysis.exposureScore(),
                sourcingPlan.mitigationPlan().mitigationOptions(),
                sourcingPlan.sourcingActions(),
                traceResult.traceNodes(),
                summary,
                LocalDateTime.now()
        );
    }
}

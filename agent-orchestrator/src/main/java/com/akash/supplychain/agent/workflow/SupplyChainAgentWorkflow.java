package com.akash.supplychain.agent.workflow;

import com.akash.supplychain.agent.agents.DisruptionDetectorAgent;
import com.akash.supplychain.agent.agents.MitigationPlannerAgent;
import com.akash.supplychain.agent.agents.ReporterAgent;
import com.akash.supplychain.agent.agents.RiskAnalyzerAgent;
import com.akash.supplychain.agent.agents.SourcingAgent;
import com.akash.supplychain.agent.agents.SupplierMapperAgent;
import com.akash.supplychain.agent.agents.TracerAgent;
import com.akash.supplychain.agent.dto.DisruptionEvent;
import com.akash.supplychain.agent.dto.InventoryDto;
import com.akash.supplychain.agent.dto.MitigationPlan;
import com.akash.supplychain.agent.dto.NewsEvent;
import com.akash.supplychain.agent.dto.RiskAnalysis;
import com.akash.supplychain.agent.dto.RiskAssessment;
import com.akash.supplychain.agent.dto.RiskReport;
import com.akash.supplychain.agent.dto.SourcingPlan;
import com.akash.supplychain.agent.dto.SupplierDto;
import com.akash.supplychain.agent.dto.TraceResult;
import com.akash.supplychain.agent.workflow.annotations.Agent;
import com.akash.supplychain.agent.workflow.annotations.AgenticWorkflow;
import com.akash.supplychain.agent.workflow.annotations.Input;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AgenticWorkflow("supply-chain-disruption-workflow")
public class SupplyChainAgentWorkflow {

    private final DisruptionDetectorAgent disruptionDetectorAgent;
    private final SupplierMapperAgent supplierMapperAgent;
    private final RiskAnalyzerAgent riskAnalyzerAgent;
    private final TracerAgent tracerAgent;
    private final MitigationPlannerAgent mitigationPlannerAgent;
    private final SourcingAgent sourcingAgent;
    private final ReporterAgent reporterAgent;

    public SupplyChainAgentWorkflow(DisruptionDetectorAgent disruptionDetectorAgent,
                                    SupplierMapperAgent supplierMapperAgent,
                                    RiskAnalyzerAgent riskAnalyzerAgent,
                                    TracerAgent tracerAgent,
                                    MitigationPlannerAgent mitigationPlannerAgent,
                                    SourcingAgent sourcingAgent,
                                    ReporterAgent reporterAgent) {
        this.disruptionDetectorAgent = disruptionDetectorAgent;
        this.supplierMapperAgent = supplierMapperAgent;
        this.riskAnalyzerAgent = riskAnalyzerAgent;
        this.tracerAgent = tracerAgent;
        this.mitigationPlannerAgent = mitigationPlannerAgent;
        this.sourcingAgent = sourcingAgent;
        this.reporterAgent = reporterAgent;
    }

    @Agent("disruption-detector")
    public DisruptionEvent detect(@Input NewsEvent news) {
        return disruptionDetectorAgent.detect(news);
    }

    @Agent("supplier-mapper")
    public RiskAssessment map(@Input DisruptionEvent event,
                              @Input("suppliers") List<SupplierDto> suppliers,
                              @Input("inventory") List<InventoryDto> inventory) {
        return supplierMapperAgent.map(event, suppliers, inventory);
    }

    @Agent("risk-analyzer")
    public RiskAnalysis analyze(@Input RiskAssessment assessment) {
        return riskAnalyzerAgent.analyze(assessment);
    }

    @Agent("tracer")
    public TraceResult trace(@Input RiskAnalysis analysis) {
        return tracerAgent.trace(analysis);
    }

    @Agent("mitigation-planner")
    public MitigationPlan plan(@Input Long supplyChainId, @Input TraceResult traceResult) {
        return mitigationPlannerAgent.plan(supplyChainId, traceResult);
    }

    @Agent("sourcing")
    public SourcingPlan source(@Input MitigationPlan mitigationPlan) {
        return sourcingAgent.execute(mitigationPlan);
    }

    @Agent("reporter")
    public RiskReport report(@Input Long supplyChainId,
                             @Input TraceResult traceResult,
                             @Input SourcingPlan sourcingPlan) {
        return reporterAgent.report(supplyChainId, traceResult, sourcingPlan);
    }
}

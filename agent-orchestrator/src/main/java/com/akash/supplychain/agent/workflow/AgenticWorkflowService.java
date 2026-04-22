package com.akash.supplychain.agent.workflow;

import com.akash.supplychain.agent.client.DataServiceClient;
import com.akash.supplychain.agent.dto.AgentStepResult;
import com.akash.supplychain.agent.dto.AnalyzeRequest;
import com.akash.supplychain.agent.dto.AnalyzeResponse;
import com.akash.supplychain.agent.dto.CreateRiskEventRequest;
import com.akash.supplychain.agent.dto.DashboardData;
import com.akash.supplychain.agent.dto.InventoryDto;
import com.akash.supplychain.agent.dto.NewsEvent;
import com.akash.supplychain.agent.dto.RiskOverviewResponse;
import com.akash.supplychain.agent.dto.RiskReport;
import com.akash.supplychain.agent.dto.SupplierDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class AgenticWorkflowService {

    private final SupplyChainAgentWorkflow workflow;
    private final DataServiceClient dataServiceClient;

    public AgenticWorkflowService(SupplyChainAgentWorkflow workflow, DataServiceClient dataServiceClient) {
        this.workflow = workflow;
        this.dataServiceClient = dataServiceClient;
    }

    public AnalyzeResponse analyze(AnalyzeRequest request) {
        String workflowId = "WF-" + UUID.randomUUID();
        List<AgentStepResult> trace = new ArrayList<>();

        NewsEvent news = new NewsEvent(
                request.eventDescription(),
                request.eventDescription(),
                request.effectiveSource(),
                request.supplyChainId(),
                request.effectiveTimestamp()
        );
        List<SupplierDto> suppliers = dataServiceClient.getSuppliers(request.supplyChainId());
        List<InventoryDto> inventory = dataServiceClient.getInventory(request.supplyChainId());

        var disruption = timed("disruption-detector", trace, () -> workflow.detect(news),
                result -> "Detected %s %s event in %s.".formatted(result.severity(), result.eventType(), result.location()));
        dataServiceClient.createRiskEvent(new CreateRiskEventRequest(
                request.supplyChainId(),
                disruption.title(),
                disruption.description(),
                disruption.location(),
                disruption.eventType(),
                disruption.severity(),
                null,
                "agent-orchestrator",
                disruption.detectedAt()
        ));

        var assessment = timed("supplier-mapper", trace, () -> workflow.map(disruption, suppliers, inventory),
                result -> result.mappingSummary());
        var analysis = timed("risk-analyzer", trace, () -> workflow.analyze(assessment),
                result -> result.analysisSummary());
        var traceResult = timed("tracer", trace, () -> workflow.trace(analysis),
                result -> result.traceSummary());
        var plan = timed("mitigation-planner", trace, () -> workflow.plan(request.supplyChainId(), traceResult),
                result -> result.plannerSummary());
        var sourcing = timed("sourcing", trace, () -> workflow.source(plan),
                result -> result.executionSummary());
        RiskReport report = timed("reporter", trace, () -> workflow.report(request.supplyChainId(), traceResult, sourcing),
                result -> "Executive report generated with %d mitigation option(s).".formatted(result.options().size()));

        return new AnalyzeResponse(workflowId, report, trace);
    }

    public DashboardData dashboardData(Long supplyChainId) {
        RiskOverviewResponse overview = dataServiceClient.getRiskOverview(supplyChainId);
        List<String> labels = overview.highRiskSuppliers().stream()
                .map(SupplierDto::name)
                .toList();
        List<Double> scores = overview.highRiskSuppliers().stream()
                .map(supplier -> switch (supplier.baselineRisk() == null ? "" : supplier.baselineRisk()) {
                    case "CRITICAL" -> 95.0;
                    case "HIGH" -> 78.0;
                    case "MEDIUM" -> 48.0;
                    default -> 22.0;
                })
                .toList();

        return new DashboardData(
                overview.supplyChainId(),
                overview.maxExposureScore(),
                overview.openRiskEvents(),
                overview.criticalOrHighSuppliers(),
                overview.riskEventsBySeverity(),
                overview.latestEvents(),
                overview.highRiskSuppliers(),
                labels,
                scores,
                LocalDateTime.now()
        );
    }

    private <T> T timed(String agentName, List<AgentStepResult> trace, Supplier<T> action,
                        java.util.function.Function<T, String> summary) {
        long start = System.currentTimeMillis();
        T result = action.get();
        long duration = System.currentTimeMillis() - start;
        trace.add(new AgentStepResult(agentName, "COMPLETED", summary.apply(result), duration, LocalDateTime.now()));
        return result;
    }
}

package com.akash.supplychain.agent.controller;

import com.akash.supplychain.agent.dto.AnalyzeRequest;
import com.akash.supplychain.agent.dto.AnalyzeResponse;
import com.akash.supplychain.agent.dto.DashboardData;
import com.akash.supplychain.agent.workflow.AgenticWorkflowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AgentOrchestratorController {

    private final AgenticWorkflowService workflowService;

    public AgentOrchestratorController(AgenticWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@Valid @RequestBody AnalyzeRequest request) {
        return workflowService.analyze(request);
    }

    @PostMapping("/disruptions/simulate")
    public AnalyzeResponse simulate(@Valid @RequestBody AnalyzeRequest request) {
        AnalyzeRequest simulationRequest = new AnalyzeRequest(
                request.eventDescription(),
                request.supplyChainId(),
                request.effectiveTimestamp(),
                "simulation-api"
        );
        return workflowService.analyze(simulationRequest);
    }

    @GetMapping("/supply-chains/{supplyChainId}/risks")
    public DashboardData risks(@PathVariable("supplyChainId") Long supplyChainId) {
        return workflowService.dashboardData(supplyChainId);
    }

    @GetMapping("/dashboard/data")
    public DashboardData dashboardData(@RequestParam(defaultValue = "1") Long supplyChainId) {
        return workflowService.dashboardData(supplyChainId);
    }
}

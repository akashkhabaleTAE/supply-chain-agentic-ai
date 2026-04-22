package com.akash.supplychain.agent;

import com.akash.supplychain.agent.dto.AnalyzeRequest;
import com.akash.supplychain.agent.workflow.AgenticWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AgentOrchestratorApplicationTests {

    @Autowired
    private AgenticWorkflowService workflowService;

    @Test
    void fullSevenAgentWorkflowReturnsRiskReportAndThreeMitigations() {
        AnalyzeRequest request = new AnalyzeRequest(
                "Hurricane Taiwan may disrupt semiconductor port operations and chip shipments",
                1L,
                null,
                "test"
        );

        var response = workflowService.analyze(request);

        assertThat(response.workflowId()).startsWith("WF-");
        assertThat(response.agentTrace()).hasSize(7);
        assertThat(response.report().exposureScore()).isGreaterThan(40.0);
        assertThat(response.report().options()).hasSize(3);
        assertThat(response.report().executiveSummary()).contains("Semiconductor");
    }

    @Test
    void dashboardDataFallsBackWhenDependentServicesAreUnavailable() {
        var dashboard = workflowService.dashboardData(1L);

        assertThat(dashboard.supplyChainId()).isEqualTo(1L);
        assertThat(dashboard.latestEvents()).isNotEmpty();
        assertThat(dashboard.riskEventsBySeverity()).containsKey("HIGH");
    }
}

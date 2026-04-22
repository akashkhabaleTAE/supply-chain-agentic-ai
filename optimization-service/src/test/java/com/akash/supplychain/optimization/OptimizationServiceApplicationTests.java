package com.akash.supplychain.optimization;

import com.akash.supplychain.optimization.dto.MitigationPlanRequest;
import com.akash.supplychain.optimization.dto.RouteOptimizationRequest;
import com.akash.supplychain.optimization.solver.MitigationPlannerService;
import com.akash.supplychain.optimization.solver.RouteOptimizerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OptimizationServiceApplicationTests {

    @Autowired
    private MitigationPlannerService mitigationPlannerService;

    @Autowired
    private RouteOptimizerService routeOptimizerService;

    @Test
    void mitigationPlanUsesFallbackInputsAndReturnsThreeRankedOptions() {
        MitigationPlanRequest request = new MitigationPlanRequest(
                1L,
                "Hurricane Taiwan",
                "Semiconductor",
                "Taiwan",
                "Pune",
                "HIGH",
                78.5,
                null,
                null,
                null
        );

        var response = mitigationPlannerService.plan(request);

        assertThat(response.rankedSuppliers()).hasSize(3);
        assertThat(response.recommendedRoute()).isNotNull();
        assertThat(response.inventoryRecommendations()).isNotEmpty();
        assertThat(response.mitigationOptions()).hasSize(3);
        assertThat(response.mitigationOptions().getFirst().confidenceScore()).isGreaterThan(0.70);
    }

    @Test
    void routeOptimizerRanksFallbackRoutes() {
        var response = routeOptimizerService.optimize(new RouteOptimizationRequest(
                "Taiwan",
                "Pune",
                "HIGH",
                78.5,
                null
        ));

        assertThat(response.rankedRoutes()).hasSize(3);
        assertThat(response.recommendedRoute().optimizationScore()).isGreaterThan(0);
    }
}

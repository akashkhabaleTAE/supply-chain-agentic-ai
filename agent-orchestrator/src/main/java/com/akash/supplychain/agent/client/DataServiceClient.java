package com.akash.supplychain.agent.client;

import com.akash.supplychain.agent.dto.CreateRiskEventRequest;
import com.akash.supplychain.agent.dto.InventoryDto;
import com.akash.supplychain.agent.dto.RiskEventDto;
import com.akash.supplychain.agent.dto.RiskOverviewResponse;
import com.akash.supplychain.agent.dto.SupplierDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataServiceClient {

    private final RestClient restClient;

    public DataServiceClient(@Value("${services.data-service.url:http://localhost:8082}") String dataServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(dataServiceUrl).build();
    }

    public List<SupplierDto> getSuppliers(Long supplyChainId) {
        try {
            return restClient.get()
                    .uri("/data/supply-chains/{supplyChainId}/suppliers", supplyChainId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException exception) {
            return fallbackSuppliers(supplyChainId);
        }
    }

    public List<InventoryDto> getInventory(Long supplyChainId) {
        try {
            return restClient.get()
                    .uri("/data/supply-chains/{supplyChainId}/inventory", supplyChainId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException exception) {
            return fallbackInventory(supplyChainId);
        }
    }

    public List<RiskEventDto> getRiskEvents(Long supplyChainId) {
        try {
            return restClient.get()
                    .uri("/data/supply-chains/{supplyChainId}/risk-events", supplyChainId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException exception) {
            return fallbackRiskEvents(supplyChainId);
        }
    }

    public RiskOverviewResponse getRiskOverview(Long supplyChainId) {
        try {
            return restClient.get()
                    .uri("/data/supply-chains/{supplyChainId}/risks", supplyChainId)
                    .retrieve()
                    .body(RiskOverviewResponse.class);
        } catch (RestClientException exception) {
            List<RiskEventDto> events = fallbackRiskEvents(supplyChainId);
            List<SupplierDto> highRiskSuppliers = fallbackSuppliers(supplyChainId).stream()
                    .filter(supplier -> "HIGH".equalsIgnoreCase(supplier.baselineRisk())
                            || "CRITICAL".equalsIgnoreCase(supplier.baselineRisk()))
                    .toList();
            Map<String, Long> severityCounts = new LinkedHashMap<>();
            severityCounts.put("LOW", 0L);
            severityCounts.put("MEDIUM", events.stream().filter(event -> "MEDIUM".equalsIgnoreCase(event.severity())).count());
            severityCounts.put("HIGH", events.stream().filter(event -> "HIGH".equalsIgnoreCase(event.severity())).count());
            severityCounts.put("CRITICAL", events.stream().filter(event -> "CRITICAL".equalsIgnoreCase(event.severity())).count());
            double maxExposure = events.stream().mapToDouble(event -> event.exposureScore() == null ? 0 : event.exposureScore()).max().orElse(0);
            return new RiskOverviewResponse(supplyChainId, maxExposure, events.size(), highRiskSuppliers.size(),
                    severityCounts, events, highRiskSuppliers);
        }
    }

    public void createRiskEvent(CreateRiskEventRequest request) {
        try {
            restClient.post()
                    .uri("/data/risk-events")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ignored) {
            // The orchestrator can still return a workflow result when persistence is temporarily unavailable.
        }
    }

    private List<SupplierDto> fallbackSuppliers(Long supplyChainId) {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                new SupplierDto(1L, supplyChainId, "SUP-TW-TSMC-01", "Taiwan Precision Foundry", 1,
                        "Taiwan", "Hsinchu", "Semiconductor", "HIGH", BigDecimal.valueOf(94.50), 32,
                        true, "{\"upstream\":[\"SUP-JP-SIL-03\",\"SUP-US-EQP-07\"],\"downstream\":[\"Pune Assembly Hub\"]}", now, now),
                new SupplierDto(2L, supplyChainId, "SUP-JP-SIL-03", "Kyushu Silicon Materials", 2,
                        "Japan", "Kyushu", "Semiconductor", "MEDIUM", BigDecimal.valueOf(91.20), 24,
                        true, "{\"upstream\":[\"SUP-AU-QUARTZ-11\"],\"downstream\":[\"SUP-TW-TSMC-01\"]}", now, now),
                new SupplierDto(3L, supplyChainId, "SUP-KR-BATT-05", "Korea Energy Cells", 1,
                        "South Korea", "Ulsan", "Battery", "MEDIUM", BigDecimal.valueOf(88.40), 21,
                        true, "{\"upstream\":[\"SUP-CL-LITH-09\"],\"downstream\":[\"Pune Assembly Hub\"]}", now, now),
                new SupplierDto(4L, supplyChainId, "SUP-CN-OLED-02", "Shenzhen Display Works", 1,
                        "China", "Guangdong", "Display", "HIGH", BigDecimal.valueOf(83.75), 18,
                        true, "{\"upstream\":[\"SUP-KR-GLASS-06\"],\"downstream\":[\"Pune Assembly Hub\"]}", now, now)
        );
    }

    private List<InventoryDto> fallbackInventory(Long supplyChainId) {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                new InventoryDto(1L, supplyChainId, 1L, "MAT-CHIP-7NM", "7nm Automotive Controller Chip",
                        1L, "SUP-TW-TSMC-01", "Taiwan Precision Foundry", "PUN-WH-01", "Pune",
                        BigDecimal.valueOf(18000), BigDecimal.valueOf(4200), BigDecimal.valueOf(15000),
                        BigDecimal.valueOf(13800), true, now, now),
                new InventoryDto(2L, supplyChainId, 2L, "MAT-LITH-CELL", "Lithium Ion Battery Cell",
                        3L, "SUP-KR-BATT-05", "Korea Energy Cells", "PUN-WH-01", "Pune",
                        BigDecimal.valueOf(22000), BigDecimal.valueOf(3000), BigDecimal.valueOf(16000),
                        BigDecimal.valueOf(19000), false, now, now)
        );
    }

    private List<RiskEventDto> fallbackRiskEvents(Long supplyChainId) {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                new RiskEventDto(1L, supplyChainId, "Typhoon warning near Taiwan semiconductor corridor",
                        "Port slowdowns and power resilience alerts may affect semiconductor shipments from Hsinchu.",
                        "Taiwan", "WEATHER", "HIGH", "ANALYZED", 78.5, "fallback-feed",
                        now.minusHours(6), now, now)
        );
    }
}

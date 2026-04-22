package com.akash.supplychain.monitoring.client;

import com.akash.supplychain.monitoring.dto.AnalyzeRequest;
import com.akash.supplychain.monitoring.dto.MonitoringEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OrchestratorClient {

    private final WebClient webClient;

    public OrchestratorClient(@Value("${services.agent-orchestrator.url:http://localhost:8081}") String orchestratorUrl) {
        this.webClient = WebClient.builder().baseUrl(orchestratorUrl).build();
    }

    public Mono<Map<String, Object>> analyze(MonitoringEventDto event) {
        AnalyzeRequest request = new AnalyzeRequest(
                event.title() + " - " + event.description(),
                event.supplyChainId(),
                event.publishedAt(),
                "monitoring-service:" + event.source()
        );

        return webClient.post()
                .uri("/api/v1/analyze")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .onErrorResume(exception -> Mono.just(fallbackAnalysis(event, exception.getMessage())));
    }

    private Map<String, Object> fallbackAnalysis(MonitoringEventDto event, String reason) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("workflowId", "MONITORING-FALLBACK-" + event.id());
        response.put("status", "ORCHESTRATOR_UNAVAILABLE");
        response.put("message", "Event stored and normalized, but orchestrator analysis could not be reached.");
        response.put("reason", reason);
        response.put("eventId", event.id());
        response.put("supplyChainId", event.supplyChainId());
        response.put("severity", event.severity());
        response.put("eventType", event.eventType());
        response.put("generatedAt", LocalDateTime.now().toString());
        return response;
    }
}

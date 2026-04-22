package com.akash.supplychain.monitoring;

import com.akash.supplychain.monitoring.dto.CreateMonitoringEventRequest;
import com.akash.supplychain.monitoring.dto.MonitoringEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MonitoringServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void manualEventIsNormalizedAndStored() {
        CreateMonitoringEventRequest request = new CreateMonitoringEventRequest(
                1L,
                "Hurricane Taiwan warning",
                "Severe weather may delay semiconductor shipments from Taiwan.",
                null,
                null,
                null,
                null,
                "test",
                null
        );

        webTestClient.post()
                .uri("/monitoring/events")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MonitoringEventDto.class)
                .value(event -> {
                    assertThat(event.id()).isPositive();
                    assertThat(event.eventType()).isEqualTo("WEATHER");
                    assertThat(event.severity()).isEqualTo("HIGH");
                    assertThat(event.materialType()).isEqualTo("Semiconductor");
                });
    }

    @Test
    void localNewsPollCreatesThreeFallbackEvents() {
        webTestClient.get()
                .uri("/monitoring/news/poll")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MonitoringEventDto.class)
                .value(events -> {
                    assertThat(events).hasSize(3);
                    assertThat(events).extracting(MonitoringEventDto::source).contains("local-news-feed");
                });
    }

    @Test
    void analyzeEndpointReturnsFallbackWhenOrchestratorUnavailable() {
        CreateMonitoringEventRequest request = new CreateMonitoringEventRequest(
                1L,
                "Port backlog in Shenzhen",
                "Container backlog may affect OLED display shipments.",
                null,
                null,
                null,
                null,
                "test",
                null
        );

        MonitoringEventDto event = webTestClient.post()
                .uri("/monitoring/events")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MonitoringEventDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(event).isNotNull();

        webTestClient.post()
                .uri("/monitoring/events/{eventId}/analyze", event.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.event.status").isEqualTo("ANALYZED")
                .jsonPath("$.analysis.workflowId").exists();
    }
}

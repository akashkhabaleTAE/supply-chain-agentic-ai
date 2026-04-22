package com.akash.supplychain.monitoring.news;

import com.akash.supplychain.monitoring.dto.CreateMonitoringEventRequest;
import com.akash.supplychain.monitoring.dto.MonitoringEventDto;
import com.akash.supplychain.monitoring.dto.MonitoringEventStatus;
import com.akash.supplychain.monitoring.dto.MonitoringSummaryResponse;
import com.akash.supplychain.monitoring.store.MonitoringEventStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class NewsIngestionService {

    private final MonitoringEventStore eventStore;

    public NewsIngestionService(MonitoringEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public Mono<MonitoringEventDto> ingest(CreateMonitoringEventRequest request) {
        MonitoringEventDto normalized = normalize(request);
        return eventStore.save(normalized);
    }

    public Flux<MonitoringEventDto> pollLocalNewsFeed() {
        return Flux.fromIterable(localFeed())
                .delayElements(Duration.ofMillis(50))
                .flatMap(this::ingest);
    }

    public Flux<MonitoringEventDto> streamEvents() {
        return eventStore.findAll();
    }

    public Mono<MonitoringEventDto> findEvent(Long eventId) {
        return eventStore.findById(eventId);
    }

    public Mono<MonitoringSummaryResponse> summary() {
        return eventStore.findAll().collectList().map(events -> {
            Map<String, Long> bySeverity = new LinkedHashMap<>();
            bySeverity.put("LOW", countSeverity(events, "LOW"));
            bySeverity.put("MEDIUM", countSeverity(events, "MEDIUM"));
            bySeverity.put("HIGH", countSeverity(events, "HIGH"));
            bySeverity.put("CRITICAL", countSeverity(events, "CRITICAL"));

            return new MonitoringSummaryResponse(
                    events.size(),
                    countStatus(events, MonitoringEventStatus.NEW),
                    countStatus(events, MonitoringEventStatus.NORMALIZED),
                    countStatus(events, MonitoringEventStatus.ANALYZED),
                    bySeverity,
                    LocalDateTime.now()
            );
        });
    }

    public Mono<MonitoringEventDto> markAnalyzed(Long eventId) {
        return eventStore.markAnalyzed(eventId);
    }

    private MonitoringEventDto normalize(CreateMonitoringEventRequest request) {
        String combinedText = (request.title() + " " + request.description()).toLowerCase(Locale.ROOT);
        String eventType = firstNonBlank(request.eventType(), detectType(combinedText));
        String severity = firstNonBlank(request.severity(), detectSeverity(combinedText));
        String location = firstNonBlank(request.location(), detectLocation(combinedText));
        String materialType = firstNonBlank(request.materialType(), detectMaterialType(combinedText));
        LocalDateTime now = LocalDateTime.now();
        String summary = "%s %s event near %s impacting %s."
                .formatted(severity, eventType, location, materialType);

        return new MonitoringEventDto(
                null,
                request.supplyChainId(),
                request.title().trim(),
                request.description().trim(),
                location,
                eventType,
                severity,
                materialType,
                firstNonBlank(request.source(), "manual-monitoring-api"),
                MonitoringEventStatus.NORMALIZED,
                request.publishedAt() == null ? now : request.publishedAt(),
                now,
                null,
                summary
        );
    }

    private List<CreateMonitoringEventRequest> localFeed() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                new CreateMonitoringEventRequest(
                        1L,
                        "Typhoon watch issued near Taiwan semiconductor corridor",
                        "Weather authorities warn of high winds that may delay chip shipments from Hsinchu and nearby ports.",
                        "Taiwan",
                        null,
                        null,
                        "Semiconductor",
                        "local-news-feed",
                        now.minusMinutes(45)
                ),
                new CreateMonitoringEventRequest(
                        1L,
                        "Shenzhen terminal reports container backlog",
                        "OLED display containers may face three day delay due to terminal congestion.",
                        "Shenzhen, China",
                        null,
                        null,
                        "Display",
                        "local-logistics-feed",
                        now.minusMinutes(35)
                ),
                new CreateMonitoringEventRequest(
                        1L,
                        "Battery cell supplier flags preventive maintenance delay",
                        "Korea Energy Cells expects a short production slowdown for lithium battery cell lots.",
                        "South Korea",
                        null,
                        null,
                        "Battery",
                        "local-supplier-feed",
                        now.minusMinutes(20)
                )
        );
    }

    private String detectType(String text) {
        if (containsAny(text, "typhoon", "hurricane", "flood", "storm", "earthquake")) {
            return "WEATHER";
        }
        if (containsAny(text, "terminal", "port", "container", "shipping", "backlog", "delay")) {
            return "LOGISTICS";
        }
        if (containsAny(text, "maintenance", "production", "supplier")) {
            return "SUPPLIER_FINANCIAL";
        }
        if (containsAny(text, "cyber", "ransomware")) {
            return "CYBER";
        }
        return "OTHER";
    }

    private String detectSeverity(String text) {
        if (containsAny(text, "shutdown", "halted", "earthquake", "critical")) {
            return "CRITICAL";
        }
        if (containsAny(text, "typhoon", "hurricane", "backlog", "high winds", "shortage")) {
            return "HIGH";
        }
        if (containsAny(text, "delay", "watch", "slowdown", "maintenance")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String detectLocation(String text) {
        if (text.contains("taiwan") || text.contains("hsinchu")) {
            return "Taiwan";
        }
        if (text.contains("shenzhen") || text.contains("china")) {
            return "Shenzhen, China";
        }
        if (text.contains("korea")) {
            return "South Korea";
        }
        if (text.contains("japan")) {
            return "Japan";
        }
        return "Unknown";
    }

    private String detectMaterialType(String text) {
        if (containsAny(text, "semiconductor", "chip", "foundry")) {
            return "Semiconductor";
        }
        if (containsAny(text, "oled", "display", "screen")) {
            return "Display";
        }
        if (containsAny(text, "battery", "lithium", "cell")) {
            return "Battery";
        }
        if (containsAny(text, "resin", "polymer")) {
            return "Polymer";
        }
        return "Semiconductor";
    }

    private boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private String firstNonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private long countStatus(List<MonitoringEventDto> events, MonitoringEventStatus status) {
        return events.stream().filter(event -> event.status() == status).count();
    }

    private long countSeverity(List<MonitoringEventDto> events, String severity) {
        return events.stream().filter(event -> severity.equalsIgnoreCase(event.severity())).count();
    }
}

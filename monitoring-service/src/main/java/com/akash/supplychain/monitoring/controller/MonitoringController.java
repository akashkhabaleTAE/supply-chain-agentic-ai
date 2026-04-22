package com.akash.supplychain.monitoring.controller;

import com.akash.supplychain.monitoring.client.OrchestratorClient;
import com.akash.supplychain.monitoring.dto.CreateMonitoringEventRequest;
import com.akash.supplychain.monitoring.dto.MonitoringEventDto;
import com.akash.supplychain.monitoring.dto.MonitoringSummaryResponse;
import com.akash.supplychain.monitoring.news.NewsIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private final NewsIngestionService newsIngestionService;
    private final OrchestratorClient orchestratorClient;

    public MonitoringController(NewsIngestionService newsIngestionService, OrchestratorClient orchestratorClient) {
        this.newsIngestionService = newsIngestionService;
        this.orchestratorClient = orchestratorClient;
    }

    @GetMapping("/events")
    public Flux<MonitoringEventDto> events() {
        return newsIngestionService.streamEvents();
    }

    @GetMapping(value = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MonitoringEventDto> eventStream() {
        return newsIngestionService.streamEvents();
    }

    @GetMapping("/events/{eventId}")
    public Mono<MonitoringEventDto> event(@PathVariable("eventId") Long eventId) {
        return newsIngestionService.findEvent(eventId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring event not found")));
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MonitoringEventDto> createEvent(@Valid @RequestBody CreateMonitoringEventRequest request) {
        return newsIngestionService.ingest(request);
    }

    @PostMapping("/events/{eventId}/analyze")
    public Mono<Map<String, Object>> analyzeEvent(@PathVariable("eventId") Long eventId) {
        return newsIngestionService.findEvent(eventId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring event not found")))
                .flatMap(event -> orchestratorClient.analyze(event)
                        .flatMap(analysis -> newsIngestionService.markAnalyzed(eventId)
                                .map(updated -> analysisResponse(updated, analysis))));
    }

    @GetMapping("/news/poll")
    public Flux<MonitoringEventDto> pollLocalNews() {
        return newsIngestionService.pollLocalNewsFeed();
    }

    @GetMapping("/summary")
    public Mono<MonitoringSummaryResponse> summary() {
        return newsIngestionService.summary();
    }

    private Map<String, Object> analysisResponse(MonitoringEventDto event, Map<String, Object> analysis) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("event", event);
        response.put("analysis", analysis);
        return response;
    }
}

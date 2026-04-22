package com.akash.supplychain.monitoring.store;

import com.akash.supplychain.monitoring.dto.MonitoringEventDto;
import com.akash.supplychain.monitoring.dto.MonitoringEventStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MonitoringEventStore {

    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<Long, MonitoringEventDto> events = new ConcurrentHashMap<>();

    public Mono<MonitoringEventDto> save(MonitoringEventDto event) {
        Long id = event.id() == null ? idSequence.getAndIncrement() : event.id();
        MonitoringEventDto persisted = new MonitoringEventDto(
                id,
                event.supplyChainId(),
                event.title(),
                event.description(),
                event.location(),
                event.eventType(),
                event.severity(),
                event.materialType(),
                event.source(),
                event.status(),
                event.publishedAt(),
                event.detectedAt(),
                event.analyzedAt(),
                event.normalizedSummary()
        );
        events.put(id, persisted);
        return Mono.just(persisted);
    }

    public Flux<MonitoringEventDto> findAll() {
        return Flux.fromIterable(events.values())
                .sort(Comparator.comparing(MonitoringEventDto::detectedAt).reversed());
    }

    public Mono<MonitoringEventDto> findById(Long eventId) {
        return Mono.justOrEmpty(Optional.ofNullable(events.get(eventId)));
    }

    public Mono<MonitoringEventDto> markAnalyzed(Long eventId) {
        MonitoringEventDto existing = events.get(eventId);
        if (existing == null) {
            return Mono.empty();
        }
        MonitoringEventDto updated = new MonitoringEventDto(
                existing.id(),
                existing.supplyChainId(),
                existing.title(),
                existing.description(),
                existing.location(),
                existing.eventType(),
                existing.severity(),
                existing.materialType(),
                existing.source(),
                MonitoringEventStatus.ANALYZED,
                existing.publishedAt(),
                existing.detectedAt(),
                LocalDateTime.now(),
                existing.normalizedSummary()
        );
        events.put(eventId, updated);
        return Mono.just(updated);
    }

    public Flux<MonitoringEventDto> findByStatus(MonitoringEventStatus status) {
        return findAll().filter(event -> event.status() == status);
    }
}

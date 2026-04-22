package com.akash.supplychain.data.service;

import com.akash.supplychain.data.dto.CreateRiskEventRequest;
import com.akash.supplychain.data.dto.RiskEventDto;
import com.akash.supplychain.data.entities.RiskEvent;
import com.akash.supplychain.data.entities.RiskEventStatus;
import com.akash.supplychain.data.entities.RiskEventType;
import com.akash.supplychain.data.entities.RiskLevel;
import com.akash.supplychain.data.repositories.RiskEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class RiskEventService {

    private final RiskEventRepository riskEventRepository;

    public RiskEventService(RiskEventRepository riskEventRepository) {
        this.riskEventRepository = riskEventRepository;
    }

    @Transactional
    public RiskEventDto createRiskEvent(CreateRiskEventRequest request) {
        RiskLevel severity = parseEnum(request.severity(), RiskLevel.class, RiskLevel.MEDIUM);
        RiskEvent event = new RiskEvent();
        event.setSupplyChainId(request.supplyChainId());
        event.setTitle(request.title().trim());
        event.setDescription(request.description().trim());
        event.setLocation(blankToNull(request.location()));
        event.setType(parseEnum(request.type(), RiskEventType.class, RiskEventType.OTHER));
        event.setSeverity(severity);
        event.setStatus(RiskEventStatus.DETECTED);
        event.setExposureScore(request.exposureScore() == null ? defaultExposureScore(severity) : request.exposureScore());
        event.setSource(blankToDefault(request.source(), "manual-api"));
        event.setDetectedAt(request.detectedAt() == null ? LocalDateTime.now() : request.detectedAt());
        return RiskEventDto.fromEntity(riskEventRepository.save(event));
    }

    private double defaultExposureScore(RiskLevel severity) {
        return switch (severity) {
            case LOW -> 20.0;
            case MEDIUM -> 45.0;
            case HIGH -> 72.0;
            case CRITICAL -> 91.0;
        };
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String blankToDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private <E extends Enum<E>> E parseEnum(String rawValue, Class<E> enumType, E fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        String normalized = rawValue.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(enumType, normalized);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported %s value '%s'".formatted(enumType.getSimpleName(), rawValue));
        }
    }
}

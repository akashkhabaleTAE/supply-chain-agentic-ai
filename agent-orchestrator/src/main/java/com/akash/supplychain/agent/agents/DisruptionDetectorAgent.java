package com.akash.supplychain.agent.agents;

import com.akash.supplychain.agent.dto.DisruptionEvent;
import com.akash.supplychain.agent.dto.NewsEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class DisruptionDetectorAgent {

    public DisruptionEvent detect(NewsEvent newsEvent) {
        String text = (newsEvent.headline() + " " + newsEvent.description()).toLowerCase(Locale.ROOT);
        String eventType = detectType(text);
        String severity = detectSeverity(text);
        String location = detectLocation(text);
        String materialType = detectMaterialType(text);
        List<String> signals = detectedSignals(text);
        double confidence = Math.min(0.95, 0.55 + signals.size() * 0.10);

        return new DisruptionEvent(
                newsEvent.headline(),
                newsEvent.description(),
                eventType,
                severity,
                location,
                materialType,
                Math.round(confidence * 100.0) / 100.0,
                signals,
                newsEvent.publishedAt() == null ? LocalDateTime.now() : newsEvent.publishedAt()
        );
    }

    private String detectType(String text) {
        if (containsAny(text, "hurricane", "typhoon", "flood", "earthquake", "cyclone", "storm")) {
            return "WEATHER";
        }
        if (containsAny(text, "port", "container", "shipping", "freight", "terminal", "backlog")) {
            return "LOGISTICS";
        }
        if (containsAny(text, "sanction", "war", "border", "tariff", "geopolitical")) {
            return "GEOPOLITICAL";
        }
        if (containsAny(text, "cyber", "ransomware", "breach")) {
            return "CYBER";
        }
        return "OTHER";
    }

    private String detectSeverity(String text) {
        if (containsAny(text, "shutdown", "earthquake", "war", "critical", "halted", "category 5")) {
            return "CRITICAL";
        }
        if (containsAny(text, "hurricane", "typhoon", "severe", "backlog", "strike", "shortage")) {
            return "HIGH";
        }
        if (containsAny(text, "delay", "warning", "watch", "slowdown")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String detectLocation(String text) {
        if (text.contains("taiwan")) {
            return "Taiwan";
        }
        if (text.contains("shenzhen") || text.contains("china")) {
            return "China";
        }
        if (text.contains("korea")) {
            return "South Korea";
        }
        if (text.contains("japan")) {
            return "Japan";
        }
        if (text.contains("india") || text.contains("pune")) {
            return "India";
        }
        return "Unknown";
    }

    private String detectMaterialType(String text) {
        if (containsAny(text, "semiconductor", "chip", "foundry", "wafer")) {
            return "Semiconductor";
        }
        if (containsAny(text, "battery", "lithium", "cell")) {
            return "Battery";
        }
        if (containsAny(text, "display", "oled", "screen")) {
            return "Display";
        }
        if (containsAny(text, "resin", "polymer", "plastic")) {
            return "Polymer";
        }
        return "Semiconductor";
    }

    private List<String> detectedSignals(String text) {
        List<String> signals = new ArrayList<>();
        if (containsAny(text, "hurricane", "typhoon", "storm", "flood")) {
            signals.add("weather-risk");
        }
        if (containsAny(text, "port", "shipping", "terminal", "container")) {
            signals.add("logistics-risk");
        }
        if (containsAny(text, "semiconductor", "chip", "foundry")) {
            signals.add("material-criticality");
        }
        if (containsAny(text, "taiwan", "china", "korea", "japan")) {
            signals.add("supplier-region-match");
        }
        return signals;
    }

    private boolean containsAny(String text, String... tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }
}

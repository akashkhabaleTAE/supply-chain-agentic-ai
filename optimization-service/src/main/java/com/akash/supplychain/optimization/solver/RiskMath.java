package com.akash.supplychain.optimization.solver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

final class RiskMath {

    private RiskMath() {
    }

    static double severityMultiplier(String severity) {
        if (severity == null || severity.isBlank()) {
            return 1.0;
        }
        return switch (severity.trim().toUpperCase(Locale.ROOT)) {
            case "LOW" -> 0.85;
            case "MEDIUM" -> 1.0;
            case "HIGH" -> 1.25;
            case "CRITICAL" -> 1.55;
            default -> 1.0;
        };
    }

    static int riskBufferDays(String severity, Double exposureScore) {
        double exposure = exposureScore == null ? 50.0 : clamp(exposureScore, 0.0, 100.0);
        int exposureBuffer = (int) Math.ceil(exposure / 20.0);
        int severityBuffer = switch (severity == null ? "" : severity.trim().toUpperCase(Locale.ROOT)) {
            case "LOW" -> 2;
            case "MEDIUM" -> 4;
            case "HIGH" -> 7;
            case "CRITICAL" -> 10;
            default -> 4;
        };
        return Math.max(severityBuffer, exposureBuffer);
    }

    static double normalizeInverse(double value, double best, double worst) {
        if (Double.compare(best, worst) == 0) {
            return 100.0;
        }
        return clamp((worst - value) / (worst - best) * 100.0, 0.0, 100.0);
    }

    static double normalizeForward(double value, double best, double worst) {
        if (Double.compare(best, worst) == 0) {
            return 100.0;
        }
        return clamp((value - worst) / (best - worst) * 100.0, 0.0, 100.0);
    }

    static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    static BigDecimal money(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    static BigDecimal quantity(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}

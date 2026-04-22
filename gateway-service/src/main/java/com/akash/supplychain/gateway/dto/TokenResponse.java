package com.akash.supplychain.gateway.dto;

import java.time.Instant;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Instant issuedAt,
        Instant expiresAt
) {
}

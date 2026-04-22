package com.akash.supplychain.gateway.security;

import java.time.Instant;
import java.util.List;

public record JwtClaims(
        String subject,
        String issuer,
        Instant issuedAt,
        Instant expiresAt,
        List<String> roles
) {
}

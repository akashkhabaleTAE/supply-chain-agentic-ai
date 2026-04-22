package com.akash.supplychain.gateway.controller;

import com.akash.supplychain.gateway.dto.LoginRequest;
import com.akash.supplychain.gateway.dto.TokenResponse;
import com.akash.supplychain.gateway.security.JwtProperties;
import com.akash.supplychain.gateway.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    public AuthController(JwtProperties jwtProperties, JwtService jwtService) {
        this.jwtProperties = jwtProperties;
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public TokenResponse token(@Valid @RequestBody LoginRequest request) {
        if (!jwtProperties.getDemoUsername().equals(request.username())
                || !jwtProperties.getDemoPassword().equals(request.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid gateway credentials");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getTtl());
        String token = jwtService.createToken(request.username(), List.of("SUPPLY_CHAIN_OPERATOR"));
        return new TokenResponse(
                token,
                "Bearer",
                jwtProperties.getTtl().toSeconds(),
                issuedAt,
                expiresAt
        );
    }
}

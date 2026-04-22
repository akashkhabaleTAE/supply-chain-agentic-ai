package com.akash.supplychain.gateway.security;

import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements WebFilter, Ordered {

    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    public JwtGatewayFilter(JwtProperties jwtProperties, JwtService jwtService) {
        this.jwtProperties = jwtProperties;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }
        if (!jwtProperties.isEnabled() || isPublic(exchange)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring("Bearer ".length());
        return jwtService.validate(token)
                .map(claims -> chain.filter(exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-Authenticated-User", claims.subject())
                                .header("X-Authenticated-Roles", String.join(",", claims.roles()))
                                .build())
                        .build()))
                .orElseGet(() -> unauthorized(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private boolean isPublic(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        return jwtProperties.getPublicPaths().stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}

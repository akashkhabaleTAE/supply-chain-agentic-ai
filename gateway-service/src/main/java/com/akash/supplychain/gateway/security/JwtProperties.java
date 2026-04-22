package com.akash.supplychain.gateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private boolean enabled = true;
    private String secret = "supplychain2026-supplychain2026-supplychain2026";
    private Duration ttl = Duration.ofHours(8);
    private String issuer = "supply-chain-agentic-ai";
    private String demoUsername = "akash";
    private String demoPassword = "supplychain2026";
    private List<String> publicPaths = List.of(
            "/actuator/health",
            "/actuator/info",
            "/api/docs",
            "/api/swagger-ui",
            "/api/webjars",
            "/api/v3/api-docs",
            "/swagger-ui",
            "/webjars",
            "/v3/api-docs",
            "/auth/token"
    );

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getDemoUsername() {
        return demoUsername;
    }

    public void setDemoUsername(String demoUsername) {
        this.demoUsername = demoUsername;
    }

    public String getDemoPassword() {
        return demoPassword;
    }

    public void setDemoPassword(String demoPassword) {
        this.demoPassword = demoPassword;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }
}

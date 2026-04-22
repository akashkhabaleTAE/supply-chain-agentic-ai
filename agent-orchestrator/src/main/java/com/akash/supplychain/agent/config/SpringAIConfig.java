package com.akash.supplychain.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    @Bean
    public AiRuntimeProperties aiRuntimeProperties(
            @Value("${ai.provider:local}") String provider,
            @Value("${ai.openai.model:gpt-4o}") String model,
            @Value("${OPENAI_API_KEY:}") String apiKey
    ) {
        boolean openAiConfigured = apiKey != null && !apiKey.isBlank();
        String mode = openAiConfigured && "openai".equalsIgnoreCase(provider) ? "openai-ready" : "local-fallback";
        return new AiRuntimeProperties(provider, model, openAiConfigured, mode);
    }

    public record AiRuntimeProperties(
            String provider,
            String model,
            boolean openAiConfigured,
            String mode
    ) {
    }
}

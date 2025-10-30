package com.project.mvprecommender.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MvpExternalClientProperties {
    @Value("${mvp.api.base-url}")
    private String baseUrl;

    @Value("${mvp.api.bootstrap-url}")
    private String bootstrapUrl;

    @Value("${mvp.api.fixtures-url}")
    private String fixturesUrl;

    @Value("${mvp.api.player-url}")
    private String playerUrl;

    @Value("${mvp.ai.openai.model}")
    private String model;

    @Value("${mvp.ai.openai.max-tokens}")
    private Integer maxTokens;

    @Value("${mvp.ai.openai.temperature}")
    private Double temperature;

    @Value("${mvp.ai.openai.api-key}")
    private String apiKey;
}

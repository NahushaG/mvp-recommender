package com.project.mvprecommender.configuration;


import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Bean
    public OpenAIClient openAIClient(MvpExternalClientProperties properties) {
        return OpenAIOkHttpClient.builder()
                .apiKey(properties.getApiKey())
                .timeout(Duration.ofSeconds(30))
                .maxRetries(4)
                .build();
    }
}

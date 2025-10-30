package com.project.mvprecommender.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(clientCodecConfigurer ->
                                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                                .build()
                );
    }

    @Bean
    public WebClient mvpWebClient(WebClient.Builder builder, MvpExternalClientProperties properties) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }


}

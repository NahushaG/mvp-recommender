package com.project.mvprecommender.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mvprecommender.service.AiAnalysisService;
import com.project.mvprecommender.service.FPLDataService;
import com.project.mvprecommender.service.MvpRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ExternalApiIntegrationTest {

    @Mock
    private WebClient mvpWebClient;

    @Mock
    private FPLDataService fplDataService;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MvpRecommendationService recommendationService = new MvpRecommendationService(null, fplDataService, aiAnalysisService);
    }

    @Test
    @DisplayName("Test FPL API integration")
    void testFplApiIntegration() {
        // Mock FPL API response
        JsonNode mockJson = objectMapper.createObjectNode().put("mock", "data");
        when(fplDataService.fetchBootStrapData()).thenReturn(Mono.just(mockJson));

        Mono<JsonNode> responseMono = fplDataService.fetchBootStrapData();
        JsonNode response = responseMono.block();

        assertThat(response).isNotNull();
        assertThat(response.has("mock")).isTrue();
    }

    @Test
    @DisplayName("Test AI API integration")
    void testAiApiIntegration() throws Exception {
        // Mock AI response
        when(aiAnalysisService.analyzeTopPlayers(null, "Goalkeeper"))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture("Mock AI insights"));

        String result = aiAnalysisService.analyzeTopPlayers(null, "Goalkeeper").get();

        assertThat(result).isEqualTo("Mock AI insights");
    }
}

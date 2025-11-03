package com.project.mvprecommender.edge;

import com.project.mvprecommender.dto.BudgetSquadRequest;
import com.project.mvprecommender.service.AiAnalysisService;
import com.project.mvprecommender.service.FPLDataService;
import com.project.mvprecommender.service.MvpRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdgeCaseTest {

    private MvpRecommendationService recommendationService;

    @Mock
    private FPLDataService fplDataService;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Mock
    private com.project.mvprecommender.repository.PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        recommendationService = new MvpRecommendationService(playerRepository, fplDataService, aiAnalysisService);
    }

    @Test
    void testMalformedResponseHandling() {
        //  Return empty player list
        when(playerRepository.findAffordablePlayersByPosition(anyInt(), anyInt()))
                .thenReturn(List.of());

        // Return non-null CompletableFuture for AI service
        when(aiAnalysisService.analyzeSquad(anyList(), anyDouble()))
                .thenReturn(CompletableFuture.completedFuture(""));

        // Create request
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(100.00)
                .formation("3-4-3")
                .build();

        // Call service
        var responseMono = recommendationService.generateBudgetSquad(request);

        // Assert
        var response = responseMono.block();
        assertThat(response).isNotNull();
    }

}

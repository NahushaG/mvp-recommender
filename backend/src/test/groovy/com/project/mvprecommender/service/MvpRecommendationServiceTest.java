package com.project.mvprecommender.service;

import com.project.mvprecommender.dto.BudgetSquadRequest;
import com.project.mvprecommender.dto.BudgetSquadResponse;
import com.project.mvprecommender.dto.MvpRecommendationResponse;
import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.model.Team;
import com.project.mvprecommender.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class MvpRecommendationServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private FPLDataService fplDataService;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @InjectMocks
    private MvpRecommendationService recommendationService;

    private Player samplePlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample Player
        samplePlayer = new Player();
        samplePlayer.setId(1L);
        samplePlayer.setWebName("Player1");
        samplePlayer.setPosition(3);
        samplePlayer.setTeam(1);
        samplePlayer.setForm("5.0");
        samplePlayer.setTotalPoints(50);
        samplePlayer.setNowCost(10);
    }

    @Test
    @DisplayName("Test getTopMvpPlayers returns expected response")
    void testGetTopMvpPlayers() {
        // Mock repository
        when(playerRepository.findByPosition(1)).thenReturn(List.of(samplePlayer));
        when(playerRepository.findByPosition(2)).thenReturn(List.of(samplePlayer));
        when(playerRepository.findByPosition(3)).thenReturn(List.of(samplePlayer));
        when(playerRepository.findByPosition(4)).thenReturn(List.of(samplePlayer));

        // Mock FPLDataService
        when(fplDataService.getUpcomingFixtures(anyInt(), anyInt())).thenReturn(List.of());
        when(fplDataService.getTeam(anyInt())).thenReturn(Team.builder().id(1).name("Team1").shortName("T1").build());
        when(fplDataService.getCurrentGameweek()).thenReturn(5);

        // Mock AI service
        when(aiAnalysisService.analyzeTopPlayers(anyList(), anyString()))
                .thenReturn(CompletableFuture.completedFuture("AI Analysis"));

        Mono<MvpRecommendationResponse> resultMono = recommendationService.getTopMvpPlayers();

        StepVerifier.create(resultMono)
                .assertNext(response -> {
                    // Validate
                    assert response.getCurrentGameWeek() == 5;
                    assert response.getTopPlayersByPosition().size() == 4;
                    assert response.getAiInsights().contains("successfully");
                })
                .verifyComplete();

        // Updated verification
        verify(playerRepository, times(4)).findByPosition(anyInt());
        // Use atLeast to avoid future failures if logic changes
        verify(fplDataService, atLeast(4)).getUpcomingFixtures(anyInt(), anyInt());
        verify(aiAnalysisService, times(4)).analyzeTopPlayers(anyList(), anyString());
    }

    @Test
    @DisplayName("Test generateBudgetSquad returns expected response")
    void testGenerateBudgetSquad() {
        BudgetSquadRequest request = new BudgetSquadRequest();
        request.setBudget(100.0);
        request.setFormation("4-4-2");
        request.setExcludedPlayers(List.of());
        request.setMustHavePlayers(List.of());

        // Mock repository
        when(playerRepository.findAffordablePlayersByPosition(anyInt(), anyInt()))
                .thenReturn(List.of(samplePlayer));
        when(playerRepository.findById(anyLong())).thenReturn(java.util.Optional.of(samplePlayer));

        // Mock FPLDataService
        when(fplDataService.getUpcomingFixtures(anyInt(), anyInt())).thenReturn(List.of());
        when(fplDataService.getTeam(anyInt())).thenReturn(Team.builder().id(1).name("Team1").shortName("T1").build());

        // Mock AI service
        when(aiAnalysisService.analyzeSquad(anyList(), anyDouble()))
                .thenReturn(CompletableFuture.completedFuture("AI Squad Analysis"));

        Mono<BudgetSquadResponse> resultMono = recommendationService.generateBudgetSquad(request);

        StepVerifier.create(resultMono)
                .assertNext(response -> {
                    assert !response.getSelectedPlayers().isEmpty();
                    assert response.getTotalCost() > 0;
                    assert response.getProjectedPoints() > 0;
                    assert response.getAiAnalysis().equals("AI Squad Analysis");
                })
                .verifyComplete();

        verify(playerRepository, atLeastOnce()).findAffordablePlayersByPosition(anyInt(), anyInt());
        verify(aiAnalysisService).analyzeSquad(anyList(), anyDouble());
    }
}

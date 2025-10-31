package com.project.mvprecommender.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mvprecommender.dto.BudgetSquadRequest;
import com.project.mvprecommender.dto.BudgetSquadResponse;
import com.project.mvprecommender.service.FPLDataService;
import com.project.mvprecommender.service.MvpRecommendationService;
import com.project.mvprecommender.service.WeeklyAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(FplController.class)
class FplControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MvpRecommendationService mvpService;

    @MockitoBean
    private WeeklyAlertService alertService;

    @MockitoBean
    private FPLDataService fplDataService;

    @Autowired
    private FplController fplController;

    @BeforeEach
    void setUp() {
        // Mock available player IDs
        Set<Long> availablePlayerIds = Set.of(1L, 2L, 3L, 4L, 5L);
        when(fplDataService.getAvailablePlayersId()).thenReturn(availablePlayerIds);
        // Ensure @PostConstruct runs after mocks are set up
        fplController.init();
    }

    /**
     *  TC1 - Valid request
     * Expected: 200 OK with a valid BudgetSquadResponse
     */
    @Test
    void testGenerateBudgetSquad_ValidRequest() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(100.0)
                .formation("4-4-2")
                .mustHavePlayers(List.of(1L, 2L))
                .excludedPlayers(List.of(3L))
                .build();

        BudgetSquadResponse response = new BudgetSquadResponse();
        response.setTotalCost(100.0);
        response.setSelectedPlayers(List.of());

        when(mvpService.generateBudgetSquad(any(BudgetSquadRequest.class)))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalCost").isEqualTo(100.0);
    }

    /**
     * TC2 - Invalid budget (< 0.1)
     * Expected: 400 BAD_REQUEST due to @DecimalMin validation failure
     */
    @Test
    void testGenerateBudgetSquad_InvalidBudget() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(0.0)
                .formation("4-4-2")
                .mustHavePlayers(List.of(1L))
                .excludedPlayers(List.of(2L))
                .build();

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * TC3 - Invalid formation format
     * Expected: 400 BAD_REQUEST with code "VALIDATION_FAILED"
     */
    @Test
    void testGenerateBudgetSquad_InvalidFormation() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(90.0)
                .formation("44-2") // Invalid pattern
                .mustHavePlayers(List.of(1L))
                .excludedPlayers(List.of(3L))
                .build();

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("VALIDATION_FAILED");
    }

    /**
     * TC4 - Overlapping players between mustHave and excluded lists
     * Expected: 400 BAD_REQUEST with code "INVALID_REQUEST"
     */
    @Test
    void testGenerateBudgetSquad_OverlappingPlayers() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(80.0)
                .formation("4-3-3")
                .mustHavePlayers(List.of(1L, 2L))
                .excludedPlayers(List.of(2L, 3L))
                .build();

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_REQUEST");
    }

    /**
     * TC5 - Must-have player not available in data service
     * Expected: 400 BAD_REQUEST with code "INVALID_REQUEST"
     */
    @Test
    void testGenerateBudgetSquad_MissingMustHavePlayer() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(70.0)
                .formation("3-4-3")
                .mustHavePlayers(List.of(99L)) // Nonexistent player
                .excludedPlayers(List.of(2L))
                .build();

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_REQUEST");
    }

    /**
     * TC6 - Excluded player not found in data service
     * Expected: 400 BAD_REQUEST with code "INVALID_REQUEST"
     */
    @Test
    void testGenerateBudgetSquad_MissingExcludedPlayer() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(60.0)
                .formation("4-4-2")
                .mustHavePlayers(List.of(1L))
                .excludedPlayers(List.of(99L))
                .build();

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_REQUEST");
    }

    /**
     * TC7 - Service throws unexpected exception
     * Expected: 500 INTERNAL_SERVER_ERROR with code "INTERNAL_ERROR"
     */
    @Test
    void testGenerateBudgetSquad_ServiceFailure() throws Exception {
        BudgetSquadRequest request = BudgetSquadRequest.builder()
                .budget(90.0)
                .formation("3-4-3")
                .mustHavePlayers(List.of(1L))
                .excludedPlayers(List.of(2L))
                .build();

        when(mvpService.generateBudgetSquad(any()))
                .thenThrow(new RuntimeException("Service failure"));

        webTestClient.post()
                .uri("/api/v1/fpl/squad/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INTERNAL_ERROR");
    }


}

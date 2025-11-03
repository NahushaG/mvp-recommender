package com.project.mvprecommender.controller;

import com.project.mvprecommender.dto.BudgetSquadRequest;
import com.project.mvprecommender.dto.BudgetSquadResponse;
import com.project.mvprecommender.dto.MvpRecommendationResponse;
import com.project.mvprecommender.dto.WeeklyAlert;
import com.project.mvprecommender.service.FPLDataService;
import com.project.mvprecommender.service.MvpRecommendationService;
import com.project.mvprecommender.service.WeeklyAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/fpl")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class FplController {

    private final MvpRecommendationService mvpService;
    private final WeeklyAlertService alertService;
    private final FPLDataService fplDataService;
    private Set<Long> availablePlayerIds;

    @PostConstruct
    public void init() {
        // Safely initialize after Spring injects dependencies
        this.availablePlayerIds = fplDataService.getAvailablePlayersId();
        log.info("Loaded {} player IDs into memory", availablePlayerIds.size());
    }

    @GetMapping("/mvp/top-players")
    @Operation(summary = "Get top MVP players", description = "Fetches top performing players for MVP recommendation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top players"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<MvpRecommendationResponse>> getTopMvpPlayers() {
        log.info("API: Getting top MVP players");
        return mvpService.getTopMvpPlayers()
                .doOnError(ex -> log.error("Failed to fetch top MVP players", ex))
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/squad/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Generate a budget squad", description = "Generates an optimal FPL squad based on budget, formation, and player constraints")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Squad generated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<BudgetSquadResponse>> generateSquad(@Valid @RequestBody BudgetSquadRequest request) {
        log.info("API: Generating squad with budget: £{}m", request.getBudget());
        request.validateBusinessRules(availablePlayerIds);
        return mvpService.generateBudgetSquad(request)
                .doOnError(ex -> log.error("Failed to generate squad", ex))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/alerts/weekly")
    @Operation(summary = "Get weekly FPL alert", description = "Retrieves the weekly alert containing key player or fixture info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Weekly alert fetched successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Mono<WeeklyAlert>> getWeeklyAlert() {
        log.info("API: Getting weekly alert");
        return ResponseEntity.ok(alertService.generateWeeklyAlert());
    }

    @PostMapping("/data/refresh")
    @Operation(summary = "Manual data refresh", description = "Triggers a manual update of FPL data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data refresh initiated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> refreshData() {
        log.info("API: Manual data refresh triggered");
        fplDataService.updateDatabase();
        return ResponseEntity.ok("Data refresh initiated");
    }

    @GetMapping("/gameWeek/current")
    @Operation(summary = "Get current gameweek", description = "Returns the current FPL gameweek")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current gameweek"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Integer> getCurrentGameWeek() {
        return ResponseEntity.ok(fplDataService.getCurrentGameweek());
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Checks if the FPL MVP Recommender service is running")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service is healthy and running")
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("FPL MVP Recommender is running");
    }
}

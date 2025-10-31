package com.project.mvprecommender.controller;

import com.project.mvprecommender.dto.BudgetSquadRequest;
import com.project.mvprecommender.dto.BudgetSquadResponse;
import com.project.mvprecommender.dto.MvpRecommendationResponse;
import com.project.mvprecommender.dto.WeeklyAlert;
import com.project.mvprecommender.service.FPLDataService;
import com.project.mvprecommender.service.MvpRecommendationService;
import com.project.mvprecommender.service.WeeklyAlertService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private  Set<Long> availablePlayerIds;

    @PostConstruct
    public void init() {
        // Safely initialize after Spring injects dependencies
        this.availablePlayerIds = fplDataService.getAvailablePlayersId();
        log.info("Loaded {} player IDs into memory", availablePlayerIds.size());
    }

    @GetMapping("/mvp/top-players")
    public Mono<ResponseEntity<MvpRecommendationResponse>> getTopMvpPlayers() {
        log.info("API: Getting top MVP players");
        return mvpService.getTopMvpPlayers()
                .doOnError(ex -> log.error("Failed to fetch top MVP players", ex))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/squad/generate")
    public Mono<ResponseEntity<BudgetSquadResponse>>generateSquad(@Valid @RequestBody BudgetSquadRequest request) {
        log.info("API: Generating squad with budget: £{}m", request.getBudget());
        request.validateBusinessRules(availablePlayerIds);
        return mvpService.generateBudgetSquad(request)
                .doOnError(ex -> log.error("Failed to generate squad", ex))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/alerts/weekly")
    public ResponseEntity<Mono<WeeklyAlert>> getWeeklyAlert() {
        log.info("API: Getting weekly alert");
        return ResponseEntity.ok(alertService.generateWeeklyAlert());
    }

    @PostMapping("/data/refresh")
    public ResponseEntity<String> refreshData() {
        log.info("API: Manual data refresh triggered");
        fplDataService.updateDatabase();
        return ResponseEntity.ok("Data refresh initiated");
    }

    @GetMapping("/gameWeek/current")
    public ResponseEntity<Integer> getCurrentGameWeek() {
        return ResponseEntity.ok(fplDataService.getCurrentGameweek());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("FPL MVP Recommender is running");
    }
}

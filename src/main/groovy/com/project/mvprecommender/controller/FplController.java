package com.project.mvprecommender.controller;

import com.project.mvprecommender.dto.BudgetSquadRequest;
import com.project.mvprecommender.dto.BudgetSquadResponse;
import com.project.mvprecommender.dto.MvpRecommendationResponse;
import com.project.mvprecommender.dto.WeeklyAlert;
import com.project.mvprecommender.service.FPLDataService;
import com.project.mvprecommender.service.MvpRecommendationService;
import com.project.mvprecommender.service.WeeklyAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/fpl")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class FplController {

    private final MvpRecommendationService mvpService;
    private final WeeklyAlertService alertService;
    private final FPLDataService fplDataService;

    @GetMapping("/mvp/top-players")
    public ResponseEntity<Mono<MvpRecommendationResponse>> getTopMvpPlayers() {
        log.info("API: Getting top MVP players");
        return ResponseEntity.ok(mvpService.getTopMvpPlayers());
    }

    @PostMapping("/squad/generate")
    public ResponseEntity<Mono<BudgetSquadResponse>>generateSquad(@RequestBody BudgetSquadRequest request) {
        log.info("API: Generating squad with budget: £{}m", request.getBudget());
        return ResponseEntity.ok(mvpService.generateBudgetSquad(request));
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

    @GetMapping("/gameweek/current")
    public ResponseEntity<Integer> getCurrentGameweek() {
        return ResponseEntity.ok(fplDataService.getCurrentGameweek());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("FPL MVP Recommender is running");
    }
}

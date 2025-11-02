package com.project.mvprecommender.service;

import com.project.mvprecommender.dto.*;
import com.project.mvprecommender.model.Fixture;
import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.model.Team;
import com.project.mvprecommender.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MvpRecommendationService {

    private final PlayerRepository playerRepository;
    private final FPLDataService fplDataService;
    private final AiAnalysisService aiAnalysisService;

    /**
     * ✅ Non-blocking version
     */
    public Mono<MvpRecommendationResponse> getTopMvpPlayers() {
        log.info("Generating MVP recommendations (async)");

        // Create parallel Monos for each position
        List<Mono<Map.Entry<String, PositionWiseRecommendation>>> positionMonos = new ArrayList<>();

        for (int position = 1; position <= 4; position++) {
            String positionName = getPositionName(position);
            List<Player> players = getTopPlayersForPosition(position, 5);

            Mono<Map.Entry<String, PositionWiseRecommendation>> mono =
                    Mono.fromFuture(aiAnalysisService.analyzeTopPlayers(players, positionName))
                            .map(aiInsights -> {
                                // Create PlayerRecommendation list WITHOUT setting aiAnalysis per player
                                List<PlayerRecommendation> recommendations = players.stream()
                                        .map(this::createPlayerRecommendation)
                                        .toList();

                                // Wrap players + aiAnalysis in PositionRecommendation
                                PositionWiseRecommendation positionRecommendation = new PositionWiseRecommendation();
                                positionRecommendation.setPlayers(recommendations);
                                positionRecommendation.setAiAnalysis(aiInsights);

                                return Map.entry(positionName, positionRecommendation);
                            });

            positionMonos.add(mono);
        }
        // Combine all async Monos into a single Mono<Map>
        return Flux.merge(positionMonos)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(topPlayersByPosition -> MvpRecommendationResponse.builder()
                        .topPlayersByPosition(topPlayersByPosition)
                        .currentGameWeek(fplDataService.getCurrentGameweek())
                        .generatedAt(LocalDateTime.now())
                        .aiInsights("Top MVP recommendations generated successfully")
                        .build());
    }

    /**
     * ✅ Async Budget Squad generation
     */
    public Mono<BudgetSquadResponse> generateBudgetSquad(BudgetSquadRequest request) {
        log.info("Generating budget squad (async) with budget: £{}m", request.getBudget());

        double budgetInTenths = request.getBudget() * 10;
        List<Player> selectedPlayers = new ArrayList<>();
        double remainingBudget = budgetInTenths;

        Map<Integer, Integer> positionRequirements = parseFormation(request.getFormation());

        // Build team within budget
        for (Map.Entry<Integer, Integer> entry : positionRequirements.entrySet()) {
            int position = entry.getKey();
            int count = entry.getValue();

            List<Player> positionPlayers = playerRepository.findAffordablePlayersByPosition(
                    (int) remainingBudget, position);
            if (positionPlayers == null) positionPlayers = List.of();
            List<Player> bestPlayers = positionPlayers.stream()
                    .filter(p -> !request.getExcludedPlayers().contains(p.getId()))
                    .peek(this::calculatePlayerMetrics)
                    .sorted((p1, p2) -> Double.compare(
                            calculateOverallScore(p2),
                            calculateOverallScore(p1)))
                    .limit(count)
                    .toList();

            selectedPlayers.addAll(bestPlayers);
            remainingBudget -= bestPlayers.stream()
                    .mapToInt(Player::getNowCost)
                    .sum();
        }

        // Add must-have players
        if (request.getMustHavePlayers() != null) {
            request.getMustHavePlayers().forEach(id -> {
                playerRepository.findById(id).ifPresent(selectedPlayers::add);
            });
        }

        List<PlayerRecommendation> recommendations = selectedPlayers.stream()
                .map(this::createPlayerRecommendation)
                .collect(Collectors.toList());

        double totalCost = selectedPlayers.stream()
                .mapToDouble(Player::getPriceInMillions)
                .sum();

        int projectedPoints = selectedPlayers.stream()
                .mapToInt(p -> p.getTotalPoints() != null ? p.getTotalPoints() : 0)
                .sum();

        // ✅ Non-blocking AI call
        return Mono.fromFuture(aiAnalysisService.analyzeSquad(selectedPlayers, request.getBudget()))
                .map(aiAnalysis -> BudgetSquadResponse.builder()
                        .selectedPlayers(recommendations)
                        .totalCost(totalCost)
                        .projectedPoints(projectedPoints)
                        .aiAnalysis(aiAnalysis)
                        .positionBreakdown(positionRequirements)
                        .build());
    }

    // --- Supporting methods (unchanged logic) ---
    public List<Player> getTopPlayersForPosition(Integer position, int limit) {
        List<Player> allPlayers = playerRepository.findByPosition(position);
        return allPlayers.stream()
                .filter(p -> !"i".equals(p.getStatus()) && !"u".equals(p.getStatus()))
                .peek(this::calculatePlayerMetrics)
                .sorted((p1, p2) -> Double.compare(calculateOverallScore(p2), calculateOverallScore(p1)))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private void calculatePlayerMetrics(Player player) {
        player.setValueForMoney(player.calculateValueForMoney());
        List<Fixture> fixtures = fplDataService.getUpcomingFixtures(player.getTeam(), 5);
        double avgDifficulty = fixtures.stream()
                .mapToInt(f -> f.getTeamHome().equals(player.getTeam()) ?
                        f.getTeamHomeDifficulty() : f.getTeamAwayDifficulty())
                .average().orElse(3.0);
        player.setFixtureScore(5.0 - avgDifficulty);
    }

    private double calculateOverallScore(Player player) {
        double formScore = parseDouble(player.getForm()) * 2.0;
        double pointsScore = (player.getTotalPoints() != null ? player.getTotalPoints() : 0) / 10.0;
        double valueScore = player.getValueForMoney() * 1.5;
        double fixtureScore = player.getFixtureScore() * 1.5;
        return formScore + pointsScore + valueScore + fixtureScore;
    }

    private PlayerRecommendation createPlayerRecommendation(Player player) {
        List<Fixture> nextFixtures = fplDataService.getUpcomingFixtures(player.getTeam(), 3);
        List<String> fixtureStrings = nextFixtures.stream()
                .limit(3)
                .map(f -> formatFixture(f, player.getTeam()))
                .collect(Collectors.toList());
        String recommendation = determineRecommendation(player);
        return PlayerRecommendation.builder()
                .playerId(player.getId())
                .name(player.getWebName())
                .team(fplDataService.getTeam(player.getTeam()).getShortName())
                .position(getPositionName(player.getPosition()))
                .price(player.getPriceInMillions())
                .totalPoints(player.getTotalPoints())
                .form(parseDouble(player.getForm()))
                .valueForMoney(player.getValueForMoney())
                .nextFixtures(fixtureStrings)
                .injuryStatus(player.getStatus())
                .chanceOfPlaying(player.getChanceOfPlayingNextRound())
                .recommendation(recommendation)
                .build();
    }

    private String formatFixture(Fixture fixture, Integer playerTeamId) {
        Team homeTeam = fplDataService.getTeam(fixture.getTeamHome());
        Team awayTeam = fplDataService.getTeam(fixture.getTeamAway());
        boolean isHome = fixture.getTeamHome().equals(playerTeamId);
        int difficulty = isHome ? fixture.getTeamHomeDifficulty() : fixture.getTeamAwayDifficulty();
        String opponent = isHome ? awayTeam.getShortName() : homeTeam.getShortName();
        String venue = isHome ? "(H)" : "(A)";
        return String.format("%s %s [Diff: %d]", opponent, venue, difficulty);
    }

    private String determineRecommendation(Player player) {
        if (player.isInjured()) return "AVOID";
        double form = parseDouble(player.getForm());
        double vfm = player.getValueForMoney();
        if (form > 5.0 && vfm > 3.0 && player.getFixtureScore() > 3.0) return "BUY";
        if (form > 3.0 && vfm > 2.0) return "HOLD";
        if (form < 2.0 || player.isDoubtful()) return "WATCH";
        return "HOLD";
    }

    private Map<Integer, Integer> parseFormation(String formation) {
        Map<Integer, Integer> requirements = new HashMap<>();
        requirements.put(1, 1);
        String[] parts = formation.split("-");
        if (parts.length == 3) {
            requirements.put(2, Integer.parseInt(parts[0]));
            requirements.put(3, Integer.parseInt(parts[1]));
            requirements.put(4, Integer.parseInt(parts[2]));
        }
        return requirements;
    }

    private String getPositionName(Integer position) {
        return switch (position) {
            case 1 -> "Goalkeeper";
            case 2 -> "Defender";
            case 3 -> "Midfielder";
            case 4 -> "Forward";
            default -> "Unknown";
        };
    }

    private double parseDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

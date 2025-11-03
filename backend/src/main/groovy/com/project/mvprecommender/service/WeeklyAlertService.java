package com.project.mvprecommender.service;

import com.project.mvprecommender.dto.InjuryUpdate;
import com.project.mvprecommender.dto.PlayerAlert;
import com.project.mvprecommender.dto.PlayerRecommendation;
import com.project.mvprecommender.dto.WeeklyAlert;
import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyAlertService {

    private final PlayerRepository playerRepository;
    private final FPLDataService fplDataService;
    private final AiAnalysisService aiAnalysisService;
    private final MvpRecommendationService mvpService;

    public Mono<WeeklyAlert> generateWeeklyAlert() {
        log.info("Generating weekly alert");

        Integer currentGameweek = fplDataService.getCurrentGameweek();

        List<PlayerAlert> playerAlerts = generatePlayerAlerts();
        List<InjuryUpdate> injuryUpdates = generateInjuryUpdates();
        List<PlayerRecommendation> mustWatchPlayers = getMustWatchPlayers();

        List<Player> topPlayers = playerRepository.findAll().stream()
                .limit(20)
                .collect(Collectors.toList());

        // Wrap CompletableFuture in a Mono
        return Mono.fromFuture(() -> aiAnalysisService.generateWeeklyInsights(topPlayers, currentGameweek))
                .map(weeklyInsights -> WeeklyAlert.builder()
                        .gameWeek(currentGameweek)
                        .playerAlerts(playerAlerts)
                        .mustWatchPlayers(mustWatchPlayers)
                        .injuryUpdates(injuryUpdates)
                        .weeklyInsights(weeklyInsights)
                        .generatedAt(LocalDateTime.now())
                        .build()
                );
    }


    private List<PlayerAlert> generatePlayerAlerts() {
        List<PlayerAlert> alerts = new ArrayList<>();
        List<Player> allPlayers = playerRepository.findAll();

        // Form drop alerts
        allPlayers.stream()
                .filter(p -> {
                    double form = parseDouble(p.getForm());
                    return form < 2.0 && p.getTotalPoints() > 50;
                })
                .limit(5)
                .forEach(p -> alerts.add(PlayerAlert.builder()
                        .playerId(p.getId())
                        .playerName(p.getWebName())
                        .alertType("FORM_DROP")
                        .message(String.format("%s form dropped to %s", p.getWebName(), p.getForm()))
                        .severity("MEDIUM")
                        .build()));

        // High ownership players to watch
        allPlayers.stream()
                .filter(p -> parseDouble(p.getSelectedByPercent()) > 30.0)
                .limit(3)
                .forEach(p -> alerts.add(PlayerAlert.builder()
                        .playerId(p.getId())
                        .playerName(p.getWebName())
                        .alertType("FIXTURE_ALERT")
                        .message(String.format("%s highly owned (%.1f%%) - monitor fixtures",
                                p.getWebName(), parseDouble(p.getSelectedByPercent())))
                        .severity("LOW")
                        .build()));

        return alerts;
    }

    private List<InjuryUpdate> generateInjuryUpdates() {
        return playerRepository.findInjuredOrDoubtfulPlayers().stream()
                .map(p -> InjuryUpdate.builder()
                        .playerId(p.getId())
                        .playerName(p.getWebName())
                        .team(fplDataService.getTeam(p.getTeam()).getShortName())
                        .injuryStatus(p.getStatus())
                        .chanceOfPlaying(p.getChanceOfPlayingNextRound())
                        .news(p.getNews())
                        .recommendation(p.isInjured() ? "TRANSFER OUT" : "MONITOR")
                        .build())
                .collect(Collectors.toList());
    }

    private List<PlayerRecommendation> getMustWatchPlayers() {
        return playerRepository.findAll().stream()
                .filter(p -> {
                    double form = parseDouble(p.getForm());
                    double ownership = parseDouble(p.getSelectedByPercent());
                    return form > 5.0 && ownership < 10.0; // High form, low ownership
                })
                .sorted((p1, p2) -> Double.compare(
                        parseDouble(p2.getForm()),
                        parseDouble(p1.getForm())))
                .limit(5)
                .map(this::createPlayerRecommendation)
                .collect(Collectors.toList());
    }

    private PlayerRecommendation createPlayerRecommendation(Player player) {
        return PlayerRecommendation.builder()
                .playerId(player.getId())
                .name(player.getWebName())
                .team(fplDataService.getTeam(player.getTeam()).getShortName())
                .position(getPositionName(player.getPosition()))
                .price(player.getPriceInMillions())
                .totalPoints(player.getTotalPoints())
                .form(parseDouble(player.getForm()))
                .recommendation("WATCH")
                .build();
    }

    private String getPositionName(Integer position) {
        return switch (position) {
            case 1 -> "GK";
            case 2 -> "DEF";
            case 3 -> "MID";
            case 4 -> "FWD";
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
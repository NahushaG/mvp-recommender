package com.project.mvprecommender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mvprecommender.configuration.MvpExternalClientProperties;
import com.project.mvprecommender.model.Fixture;
import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.model.Team;
import com.project.mvprecommender.repository.FixtureRepository;
import com.project.mvprecommender.repository.PlayerRepository;
import com.project.mvprecommender.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FPLDataService {
    private final WebClient mvpWebClient;
    private final MvpExternalClientProperties properties;
    private final ObjectMapper objectMapper;
    private final PlayerRepository playerRepository;
    private final FixtureRepository fixtureRepository;
    private final TeamRepository teamRepository;

    @Cacheable(value = "fplData", key = "'bootstrap'")
    public Mono<JsonNode> fetchBootStrapData() {
        log.info("Fetching FPL bootstrap data");
        return mvpWebClient
                .get()
                .uri(properties.getBootstrapUrl())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnSuccess(data -> log.info("Successfully fetched boostrap data"))
                .doOnError(error -> log.error("Error fetching bootstrap data", error));

    }

    public void updateDatabase() {
        fetchBootStrapData()
                .subscribe(data -> {
                    try {
                        updateTeams(data.get("teams"));
                        updatePlayers(data.get("elements"));
                        updateFixtures();
                        log.info("Database updated successfully");
                    } catch (Exception e) {
                        log.error("Error updating database", e);
                    }
                });
    }

    private void updateTeams(JsonNode teamsNode) {
        try {
            List<Team> teams = objectMapper.convertValue(
                    teamsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Team.class)
            );
            teamRepository.saveAll(teams);
            log.info("Updated {} teams", teams.size());
        } catch (Exception e) {
            log.error("Error updating teams", e);
        }
    }

    private void updatePlayers(JsonNode playersNode) {
        try {
            List<Player> players = objectMapper.convertValue(
                    playersNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class)
            );

            // Set last updated timestamp
            players.forEach(player -> player.setLastUpdated(LocalDateTime.now()));

            playerRepository.saveAll(players);
            log.info("Updated {} players", players.size());
        } catch (Exception e) {
            log.error("Error updating players", e);
        }
    }

    private void updateFixtures() {
        mvpWebClient
                .get()
                .uri(properties.getFixturesUrl())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .subscribe(data -> {
                    try {
                        List<Fixture> fixtures = objectMapper.convertValue(
                                data,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Fixture.class)
                        );
                        fixtureRepository.saveAll(fixtures);
                        log.info("Updated {} fixtures", fixtures.size());
                    } catch (Exception e) {
                        log.error("Error updating fixtures", e);
                    }
                });
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public List<Player> getPlayersByPosition(Integer position) {
        return playerRepository.findByPosition(position);
    }

    public List<Fixture> getUpcomingFixtures(Integer teamId, int numFixtures) {
        Integer currentGw = getCurrentGameweek();
        return fixtureRepository.findTeamFixtures(teamId, currentGw, currentGw + numFixtures);
    }

    public Integer getCurrentGameweek() {
        // Logic to determine current gameweek
        List<Fixture> fixtures = fixtureRepository.findAll();
        return fixtures.stream()
                .filter(f -> !f.getFinished())
                .map(Fixture::getGameWeek)
                .min(Integer::compareTo)
                .orElse(1);
    }

    public Team getTeam(Integer teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }
}

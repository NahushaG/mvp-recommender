package com.project.mvprecommender.nfr;

import com.project.mvprecommender.model.Fixture;
import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.model.Team;
import com.project.mvprecommender.repository.FixtureRepository;
import com.project.mvprecommender.repository.PlayerRepository;
import com.project.mvprecommender.repository.TeamRepository;
import com.project.mvprecommender.service.MvpRecommendationService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // <-- Important
class PerformanceTest {

    @Autowired
    private MvpRecommendationService mvpRecommendationService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private FixtureRepository fixtureRepository;

    @Autowired
    private TeamRepository teamRepository;

    private long serviceThresholdMs;

    @BeforeAll
    void setUpEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("OPENAI_API_KEY", dotenv.get("OPENAI_API_KEY"));
        System.setProperty("MVP_AI_MODEL", dotenv.get("MVP_AI_MODEL"));
        System.setProperty("MVP_AI_MAX_TOKENS", dotenv.get("MVP_AI_MAX_TOKENS"));
        System.setProperty("MVP_AI_TEMPERATURE", dotenv.get("MVP_AI_TEMPERATURE"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DDL_AUTO", "none");
        System.setProperty("SHOW_SQL", dotenv.get("SHOW_SQL"));

        serviceThresholdMs = Long.parseLong(dotenv.get("SERVICE_PERFORMANCE_THRESHOLD_MS", "8000"));

        // Clear DB
        playerRepository.deleteAll();
        fixtureRepository.deleteAll();

        // Setup test players
        playerRepository.saveAll(List.of(
                Player.builder()
                        .id(1L)
                        .firstName("Player1")
                        .position(1)
                        .team(1)
                        .nowCost(10)
                        .goalsScored(5)
                        .assists(2)
                        .minutes(900)
                        .influence("12.0")
                        .ictIndex("18.7")
                        .totalPoints(50)
                        .build(),
                Player.builder()
                        .id(2L)
                        .firstName("Player2")
                        .position(2)
                        .team(2)
                        .nowCost(20)
                        .goalsScored(3)
                        .assists(4)
                        .minutes(850)
                        .influence("12.0")
                        .ictIndex("18.7")
                        .totalPoints(60)
                        .build()
        ));

        // Setup test fixtures
        fixtureRepository.saveAll(List.of(
                Fixture.builder()
                        .id(1L)
                        .gameWeek(1)
                        .kickoffTime(LocalDateTime.now().toString())
                        .teamHome(1)
                        .teamAway(2)
                        .finished(false)    // <-- important
                        .started(true)      // <-- optional, depending on your logic
                        .build(),
                Fixture.builder()
                        .id(2L)
                        .gameWeek(1)
                        .kickoffTime(LocalDateTime.now().toString())
                        .teamHome(2)
                        .teamAway(1)
                        .finished(false)    // <-- important
                        .started(true)
                        .build()
        ));

        teamRepository.saveAll(List.of(
                Team.builder().id(1).shortName("HOME").build(),
                Team.builder().id(2).shortName("AWAY").build()
        ));

    }

    @Test
    void measureDbQueryPerformance() {
        // Warm-up
        for (int position = 1; position <= 4; position++) {
            mvpRecommendationService.getTopPlayersForPosition(position, 5);
        }

        int runs = 5;
        long total = 0;
        for (int i = 0; i < runs; i++) {
            Instant start = Instant.now();
            for (int position = 1; position <= 4; position++) {
                mvpRecommendationService.getTopPlayersForPosition(position, 5);
            }
            Instant end = Instant.now();
            total += Duration.between(start, end).toMillis();
        }

        long average = total / runs;
        System.out.println("Average DB + in-memory processing duration: " + average + " ms");

        long threshold = 5000; // default DB threshold
        assertTrue(average < threshold,
                "Average DB query + processing took too long (> " + threshold + " ms)");
    }

    @Test
    void measureFullServicePerformance() {
        // Warm-up
        mvpRecommendationService.getTopMvpPlayers().block();

        int runs = 3; // fewer runs for heavier processing
        long total = 0;
        for (int i = 0; i < runs; i++) {
            Instant start = Instant.now();
            Mono<?> result = mvpRecommendationService.getTopMvpPlayers();
            result.block(Duration.ofSeconds(10)); // timeout per run
            Instant end = Instant.now();
            total += Duration.between(start, end).toMillis();
        }

        long average = total / runs;
        System.out.println("Average full service duration: " + average + " ms");
        assertTrue(average < serviceThresholdMs,
                "Average full service took too long (> " + serviceThresholdMs + " ms)");
    }
}

package com.project.mvprecommender.nfr;

import com.project.mvprecommender.repository.PlayerRepository;
import com.project.mvprecommender.service.MvpRecommendationService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PerformanceTest {

    @Autowired
    private MvpRecommendationService mvpRecommendationService;

    @Autowired
    private PlayerRepository playerRepository;

    private static long serviceThresholdMs;

    @BeforeAll
    static void setUpEnv() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        System.setProperty("OPENAI_API_KEY", dotenv.get("OPENAI_API_KEY"));
        System.setProperty("MVP_AI_MODEL", dotenv.get("MVP_AI_MODEL"));
        System.setProperty("MVP_AI_MAX_TOKENS", dotenv.get("MVP_AI_MAX_TOKENS"));
        System.setProperty("MVP_AI_TEMPERATURE", dotenv.get("MVP_AI_TEMPERATURE"));
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DDL_AUTO", "none");
        System.setProperty("SHOW_SQL", dotenv.get("SHOW_SQL"));

        long dbThresholdMs = Long.parseLong(dotenv.get("DB_PERFORMANCE_THRESHOLD_MS", "500"));
        serviceThresholdMs = Long.parseLong(dotenv.get("SERVICE_PERFORMANCE_THRESHOLD_MS", "8000"));
    }

    /**
     * Measure **pure database query** performance (repository-level).
     */
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

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        long threshold = Long.parseLong(dotenv.get("DB_PERFORMANCE_THRESHOLD_MS", "5000"));

        assertTrue(average < threshold,
                "Average DB query + processing took too long (> " + threshold + " ms)");
    }

    /**
     * Measure full service (DB + AI + processing) performance.
     */
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

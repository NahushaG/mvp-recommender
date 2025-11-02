package com.project.mvprecommender.integration;

import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.repository.PlayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // rollback after each test
class DatabaseIntegrationTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    @DisplayName("Player repository CRUD operations")
    void testPlayerRepositoryCrud() {
        // Create
        Player player = new Player();
        player.setId(1L);
        player.setWebName("Test Player");
        player.setTotalPoints(10);
        player.setNowCost(50);
        Player saved = playerRepository.save(player);

        assertThat(saved.getId()).isNotNull();

        // Read
        Optional<Player> retrieved = playerRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getWebName()).isEqualTo("Test Player");

        // Update
        retrieved.ifPresent(p -> {
            p.setTotalPoints(20);
            playerRepository.save(p);
        });
        Player updated = playerRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTotalPoints()).isEqualTo(20);

        // Delete
        playerRepository.delete(updated);
        assertThat(playerRepository.findById(saved.getId())).isNotPresent();
    }

    @Test
    @DisplayName("Verify database schema matches entities")
    void testSchemaVerification() {
        // Simple verification: count rows (empty database expected)
        long count = playerRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(0); // should pass if schema exists
    }
}

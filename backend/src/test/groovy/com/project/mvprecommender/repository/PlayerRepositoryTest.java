package com.project.mvprecommender.repository;

import com.project.mvprecommender.model.Player;
import com.project.mvprecommender.model.helper.PlayerGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        // Clear DB before each test
        playerRepository.deleteAll();

        // Generate sample players
        List<Player> players = PlayerGenerator.generateSamplePlayers();
        player1 = players.get(0);
        player2 = players.get(1);
        player3 = players.get(2);

        playerRepository.saveAll(players);
    }

    @Test
    @DisplayName("Find players by position")
    void testFindByPosition() {
        List<Player> position1Players = playerRepository.findByPosition(1);

        // Ignore teamName and positionName for DB comparison
        assertThat(position1Players)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("teamName", "positionName")
                .containsExactly(player2);
    }

    @Test
    @DisplayName("Find injured or doubtful players")
    void testFindInjuredOrDoubtfulPlayers() {
        // Mark player1 as injured
        player1.setStatus("i");
        playerRepository.save(player1);

        List<Player> injured = playerRepository.findInjuredOrDoubtfulPlayers();

        assertThat(injured)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("teamName", "positionName")
                .containsExactly(player1);
    }

    @Test
    @DisplayName("Get all player IDs")
    void testGetAllPlayerIds() {
        var ids = playerRepository.getAllPlayerIds();
        assertThat(ids).containsExactlyInAnyOrder(player1.getId(), player2.getId(),player3.getId());
    }
}

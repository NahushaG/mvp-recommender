package com.project.mvprecommender.repository;

import com.project.mvprecommender.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();

        team1 = Team.builder()
                .id(1)
                .name("Liverpool")
                .shortName("LIV")
                .build();

        team2 = Team.builder()
                .id(2)
                .name("Manchester City")
                .shortName("MCI")
                .build();

        teamRepository.saveAll(List.of(team1, team2));
    }

    @Test
    @DisplayName("Find team by exact name")
    void testFindByName() {
        Team result = teamRepository.findByName("Liverpool");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Return null if team name does not exist")
    void testFindByNameNotFound() {
        Team result = teamRepository.findByName("Arsenal");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Find all teams")
    void testFindAllTeams() {
        List<Team> allTeams = teamRepository.findAll();
        assertThat(allTeams).hasSize(2).containsExactlyInAnyOrder(team1, team2);
    }
}

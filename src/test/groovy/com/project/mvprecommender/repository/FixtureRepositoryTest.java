package com.project.mvprecommender.repository;

import com.project.mvprecommender.model.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FixtureRepositoryTest {

    @Autowired
    private FixtureRepository fixtureRepository;

    private Fixture fixture1;
    private Fixture fixture2;
    private Fixture fixture3;

    @BeforeEach
    void setUp() {
        // Clear DB before each test
        fixtureRepository.deleteAll();

        // Create sample fixtures
        fixture1 = Fixture.builder()
                .id(1L)
                .gameWeek(1)
                .teamHome(1)
                .teamAway(2)
                .kickoffTime(String.valueOf(LocalDateTime.now()))
                .build();

        fixture2 = Fixture.builder()
                .id(2L)
                .gameWeek(2)
                .teamHome(2)
                .teamAway(3)
                .kickoffTime(String.valueOf(LocalDateTime.now().plusDays(1)))
                .build();

        fixture3 = Fixture.builder()
                .id(3L)
                .gameWeek(3)
                .teamHome(1)
                .teamAway(3)
                .kickoffTime(String.valueOf(LocalDateTime.now().plusDays(2)))
                .build();

        fixtureRepository.saveAll(List.of(fixture1, fixture2, fixture3));
    }

    @Test
    @DisplayName("Find fixtures by game week")
    void testFindByGameWeek() {
        List<Fixture> gw2Fixtures = fixtureRepository.findByGameWeek(2);

        assertThat(gw2Fixtures)
                .containsExactly(fixture2);
    }

    @Test
    @DisplayName("Find fixtures for a team within a range of game weeks")
    void testFindTeamFixtures() {
        List<Fixture> team1Fixtures = fixtureRepository.findTeamFixtures(1, 1, 3);

        // fixture1 and fixture3 involve team 1 and are in game week 1-3
        assertThat(team1Fixtures)
                .containsExactlyInAnyOrder(fixture1, fixture3);
    }

    @Test
    @DisplayName("No fixtures returned if outside game week range")
    void testNoFixturesForOutOfRangeGameWeek() {
        List<Fixture> fixtures = fixtureRepository.findTeamFixtures(1, 4, 5);
        assertThat(fixtures).isEmpty();
    }
}

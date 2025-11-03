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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FPLDataServiceTest {

    @Mock
    private WebClient mvpWebClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private MvpExternalClientProperties properties;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private FixtureRepository fixtureRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private FPLDataService fplDataService;

    @Captor
    private ArgumentCaptor<List<Player>> playerListCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Fetch bootstrap data from WebClient")
    void testFetchBootStrapData() {
        // Mock WebClient fluent API chain correctly
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        JsonNode mockJson = mock(JsonNode.class);

        when(mvpWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(Mono.just(mockJson));
        when(properties.getBootstrapUrl()).thenReturn("/bootstrap");

        JsonNode result = fplDataService.fetchBootStrapData().block();

        assertThat(result).isNotNull();
        verify(mvpWebClient).get();
        verify(requestHeadersUriSpec).uri("/bootstrap");
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(JsonNode.class);
    }

    @Test
    @DisplayName("Get all players")
    void testGetAllPlayers() {
        List<Player> players = List.of(new Player(), new Player());
        when(playerRepository.findAll()).thenReturn(players);

        List<Player> result = fplDataService.getAllPlayers();

        assertThat(result).hasSize(2);
        verify(playerRepository).findAll();
    }

    @Test
    @DisplayName("Get available player IDs")
    void testGetAvailablePlayersId() {
        Set<Long> ids = Set.of(1L, 2L, 3L);
        when(playerRepository.getAllPlayerIds()).thenReturn(ids);

        Set<Long> result = fplDataService.getAvailablePlayersId();
        assertThat(result).containsExactlyInAnyOrder(1L, 2L, 3L);
        verify(playerRepository).getAllPlayerIds();
    }

    @Test
    @DisplayName("Get players by position")
    void testGetPlayersByPosition() {
        List<Player> forwards = List.of(new Player(), new Player());
        when(playerRepository.findByPosition(4)).thenReturn(forwards);

        List<Player> result = fplDataService.getPlayersByPosition(4);
        assertThat(result).hasSize(2);
        verify(playerRepository).findByPosition(4);
    }

    @Test
    @DisplayName("Get current gameweek when fixtures exist")
    void testGetCurrentGameweek() {
        Fixture fixture1 = new Fixture();
        fixture1.setGameWeek(5);
        fixture1.setFinished(false);

        Fixture fixture2 = new Fixture();
        fixture2.setGameWeek(6);
        fixture2.setFinished(false);

        when(fixtureRepository.findAll()).thenReturn(List.of(fixture1, fixture2));

        Integer currentGw = fplDataService.getCurrentGameweek();
        assertThat(currentGw).isEqualTo(5);
    }

    @Test
    @DisplayName("Get team by ID")
    void testGetTeam() {
        Team team = Team.builder()
                .id(1)
                .name("Liverpool")
                .shortName("LIV")
                .build();
        when(teamRepository.findById(1)).thenReturn(java.util.Optional.of(team));

        Team result = fplDataService.getTeam(1);
        assertThat(result.getName()).isEqualTo("Liverpool");
        verify(teamRepository).findById(1);
    }
}

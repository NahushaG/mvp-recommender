package com.project.mvprecommender.model.helper;

import com.project.mvprecommender.model.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlayerGenerator {

    public static List<Player> generateSamplePlayers() {
        List<Player> players = new ArrayList<>();

        players.add(Player.builder()
                .id(1L)
                .webName("Salah")
                .firstName("Mohamed")
                .secondName("Salah")
                .team(1)
                .teamName("Liverpool")
                .position(4)
                .positionName("FWD")
                .nowCost(125) // 12.5m
                .totalPoints(210)
                .form("7.2")
                .pointsPerGame("6.5")
                .selectedByPercent("45.5")
                .goalsScored(22)
                .assists(5)
                .cleanSheets(0)
                .bonus(18)
                .minutes(2700)
                .yellowCards(2)
                .redCards(0)
                .influence("85.0")
                .creativity("78.5")
                .threat("90.0")
                .ictIndex("70.5")
                .expectedGoals("20.5")
                .expectedAssists("6.0")
                .expectedGoalInvolvements("26.5")
                .expectedGoalsConceded("0.0")
                .status("a")
                .news("")
                .chanceOfPlayingNextRound(100)
                .lastUpdated(LocalDateTime.now())
                .build());

        players.add(Player.builder()
                .id(2L)
                .webName("Ederson")
                .firstName("Ederson")
                .secondName("Moraes")
                .team(2)
                .teamName("Manchester City")
                .position(1)
                .positionName("GK")
                .nowCost(65) // 6.5m
                .totalPoints(160)
                .form("5.0")
                .pointsPerGame("4.5")
                .selectedByPercent("12.3")
                .goalsScored(0)
                .assists(1)
                .cleanSheets(15)
                .bonus(12)
                .minutes(3420)
                .saves(80)
                .goalsConceded(25)
                .yellowCards(1)
                .redCards(0)
                .influence("70.0")
                .creativity("55.0")
                .threat("10.0")
                .ictIndex("45.0")
                .expectedGoals("0.0")
                .expectedAssists("1.5")
                .expectedGoalInvolvements("1.5")
                .expectedGoalsConceded("20.0")
                .status("a")
                .news("")
                .chanceOfPlayingNextRound(100)
                .lastUpdated(LocalDateTime.now())
                .build());

        players.add(Player.builder()
                .id(3L)
                .webName("Kane")
                .firstName("Harry")
                .secondName("Kane")
                .team(3)
                .teamName("Tottenham")
                .position(4)
                .positionName("FWD")
                .nowCost(120) // 12.0m
                .totalPoints(190)
                .form("6.8")
                .pointsPerGame("5.5")
                .selectedByPercent("38.0")
                .goalsScored(21)
                .assists(13)
                .cleanSheets(0)
                .bonus(15)
                .minutes(2800)
                .yellowCards(3)
                .redCards(0)
                .influence("80.0")
                .creativity("85.0")
                .threat("88.0")
                .ictIndex("68.5")
                .expectedGoals("22.0")
                .expectedAssists("10.0")
                .expectedGoalInvolvements("32.0")
                .expectedGoalsConceded("0.0")
                .status("a")
                .news("")
                .chanceOfPlayingNextRound(100)
                .lastUpdated(LocalDateTime.now())
                .build());

        return players;
    }

}
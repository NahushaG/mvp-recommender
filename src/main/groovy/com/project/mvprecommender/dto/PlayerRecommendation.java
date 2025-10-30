package com.project.mvprecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRecommendation {
    private Long playerId;
    private String name;
    private String team;
    private String position;
    private Double price;
    private Integer totalPoints;
    private Double form;
    private Double valueForMoney;
    private Integer upcomingFixtureDifficulty;
    private List<String> nextFixtures;
    private String injuryStatus;
    private Integer chanceOfPlaying;
    private Double aiScore;
    //private String aiAnalysis;
    private String recommendation; // BUY, HOLD, SELL, WATCH
}

package com.project.mvprecommender.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name= "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("web_name")
    private String webName;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("second_name")
    private String secondName;

    @JsonProperty("team")
    private Integer team;

    @JsonProperty("team_name")
    @Transient
    private String teamName;

    @JsonProperty("element_type")
    private Integer position; // 1=GK, 2=DEF, 3=MID, 4=FWD

    @Transient
    private String positionName;

    @JsonProperty("now_cost")
    private Integer nowCost; // Price in 0.1m units

    @JsonProperty("total_points")
    private Integer totalPoints;

    @JsonProperty("form")
    private String form;

    @JsonProperty("points_per_game")
    private String pointsPerGame;

    @JsonProperty("selected_by_percent")
    private String selectedByPercent;

    @JsonProperty("goals_scored")
    private Integer goalsScored;

    @JsonProperty("assists")
    private Integer assists;

    @JsonProperty("clean_sheets")
    private Integer cleanSheets;

    @JsonProperty("bonus")
    private Integer bonus;

    @JsonProperty("minutes")
    private Integer minutes;

    @JsonProperty("saves")
    private Integer saves;

    @JsonProperty("goals_conceded")
    private Integer goalsConceded;

    @JsonProperty("yellow_cards")
    private Integer yellowCards;

    @JsonProperty("red_cards")
    private Integer redCards;

    @JsonProperty("influence")
    private String influence;

    @JsonProperty("creativity")
    private String creativity;

    @JsonProperty("threat")
    private String threat;

    @JsonProperty("ict_index")
    private String ictIndex;

    @JsonProperty("expected_goals")
    private String expectedGoals;

    @JsonProperty("expected_assists")
    private String expectedAssists;

    @JsonProperty("expected_goal_involvements")
    private String expectedGoalInvolvements;

    @JsonProperty("expected_goals_conceded")
    private String expectedGoalsConceded;

    @JsonProperty("status")
    private String status; // a=available, d=doubtful, i=injured, u=unavailable

    @JsonProperty("news")
    private String news;

    @JsonProperty("chance_of_playing_next_round")
    private Integer chanceOfPlayingNextRound;

    private LocalDateTime lastUpdated;

    // Calculated fields
    @Transient
    private Double valueForMoney; // Points per million

    @Transient
    private Double fixtureScore; // Difficulty score for upcoming fixtures

    @Transient
    private Double aiRecommendationScore;

    @Transient
    private String aiAnalysis;

    public Double getPriceInMillions() {
        return nowCost / 10.0;
    }

    public Double calculateValueForMoney() {
        if (nowCost == null || nowCost == 0) return 0.0;
        return (totalPoints != null ? totalPoints : 0) / (nowCost / 10.0);
    }

    public boolean isInjured() {
        return "i".equals(status) || "u".equals(status);
    }

    public boolean isDoubtful() {
        return "d".equals(status);
    }
}

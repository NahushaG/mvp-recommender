package com.project.mvprecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InjuryUpdate {
    private Long playerId;
    private String playerName;
    private String team;
    private String injuryStatus;
    private Integer chanceOfPlaying;
    private String news;
    private String recommendation;
}

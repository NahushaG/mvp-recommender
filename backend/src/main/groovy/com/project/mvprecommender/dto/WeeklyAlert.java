package com.project.mvprecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyAlert {
    private Integer gameWeek;
    private List<PlayerAlert> playerAlerts;
    private List<PlayerRecommendation> mustWatchPlayers;
    private List<InjuryUpdate> injuryUpdates;
    private String weeklyInsights;
    private LocalDateTime generatedAt;
}

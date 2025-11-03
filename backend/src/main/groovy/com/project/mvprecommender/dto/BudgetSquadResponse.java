package com.project.mvprecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSquadResponse {
    private List<PlayerRecommendation> selectedPlayers;
    private Double totalCost;
    private Integer projectedPoints;
    private String aiAnalysis;
    private Map<Integer, Integer> positionBreakdown;
}

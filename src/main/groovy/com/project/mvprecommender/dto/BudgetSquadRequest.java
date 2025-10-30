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
public class BudgetSquadRequest {
    private Double budget; // Budget in millions
    private String formation; // e.g., "3-4-3", "4-4-2"
    private List<Long> mustHavePlayers;
    private List<Long> excludedPlayers;
}

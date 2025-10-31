package com.project.mvprecommender.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSquadRequest {
    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.1", message = "Budget must be at least 0.1 million")
    @DecimalMax(value = "100", message = "Budget can not exceed 100 million")
    private Double budget; // Budget in millions
    @NotBlank(message = "Formation is required")
    @Pattern(regexp = "^[1-9]-[1-9]-[1-9]$", message = "Formation must be in format X-X-X, e.g., 4-4-2")
    private String formation; // e.g., "3-4-3", "4-4-2"
    private List<@NotNull(message = "Player ID cannot be null") Long> mustHavePlayers;
    private List<@NotNull(message = "Player ID cannot be null") Long> excludedPlayers;

    public void validateBusinessRules(Set<Long> availablePlayerIds) {
        // Check overlapping players
        if (mustHavePlayers != null && excludedPlayers != null) {
            for (Long p : mustHavePlayers) {
                if (excludedPlayers.contains(p)) {
                    throw new IllegalArgumentException("Player ID " + p + " cannot be both must-have and excluded.");
                }
            }
        }

        // Check if all player IDs exist in your dataset
        if (mustHavePlayers != null) {
            for (Long p : mustHavePlayers) {
                if (!availablePlayerIds.contains(p)) {
                    throw new IllegalArgumentException("Must-have player ID " + p + " does not exist.");
                }
            }
        }

        if (excludedPlayers != null) {
            for (Long p : excludedPlayers) {
                if (!availablePlayerIds.contains(p)) {
                    throw new IllegalArgumentException("Excluded player ID " + p + " does not exist.");
                }
            }
        }
    }
}

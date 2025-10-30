package com.project.mvprecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerAlert {
    private Long playerId;
    private String playerName;
    private String alertType; // FORM_DROP, PRICE_RISE, PRICE_FALL, FIXTURE_ALERT
    private String message;
    private String severity; // HIGH, MEDIUM, LOW
}

package com.project.mvprecommender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MvpRecommendationResponse {
    private Map<String, PositionWiseRecommendation> topPlayersByPosition;
    private Integer currentGameWeek;
    private LocalDateTime generatedAt;
    private String aiInsights;
}

package com.project.mvprecommender.service;

import com.project.mvprecommender.dto.WeeklyAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    public void sendWeeklyAlert(WeeklyAlert alert) {
        log.info("Sending weekly alert for gameweek {}", alert.getGameWeek());

        // Email notification logic here
        // For learning purposes, just log
        log.info("Weekly Alert Summary:");
        log.info("- Player Alerts: {}", alert.getPlayerAlerts().size());
        log.info("- Injury Updates: {}", alert.getInjuryUpdates().size());
        log.info("- Must Watch Players: {}", alert.getMustWatchPlayers().size());
        log.info("AI Insights: {}", alert.getWeeklyInsights());
    }
}

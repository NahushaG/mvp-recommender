package com.project.mvprecommender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final FPLDataService fplDataService;
    private final WeeklyAlertService weeklyAlertService;
    private final NotificationService notificationService;

    @Scheduled(cron = "${mvp.scheduler.update-data-cron}")
    public void updateFplData() {
        log.info("Scheduled FPL data update started");
        try {
            fplDataService.updateDatabase();
            log.info("FPL data update completed successfully");
        } catch (Exception e) {
            log.error("Error updating FPL data", e);
        }
    }

    @Scheduled(cron = "${mvp.scheduler.weekly-alert-cron}")
    public void generateWeeklyAlerts() {
        log.info("Generating weekly alerts");
        weeklyAlertService.generateWeeklyAlert()
                .doOnNext(alert -> {
                    notificationService.sendWeeklyAlert(alert);
                    log.info("Weekly alerts generated and sent");
                })
                .doOnError(e -> log.error("Error generating weekly alerts", e))
                .subscribe(); // triggers the async pipeline
    }
}

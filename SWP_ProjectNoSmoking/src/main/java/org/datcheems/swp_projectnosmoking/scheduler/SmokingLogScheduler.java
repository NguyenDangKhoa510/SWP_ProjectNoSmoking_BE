package org.datcheems.swp_projectnosmoking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.service.SmokingLogService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmokingLogScheduler {

    private final SmokingLogService smokingLogService;

    /**
     * Scheduled task that runs daily at 23:00 PM to check for members who haven't logged their smoking data
     * for the previous day and send them notifications.
     */
    @Scheduled(cron = "0 0 23 * * ?") // Run every day
    public void checkMissingSmokingLogs() {
        log.info("Starting scheduled task to check for missing smoking logs");
        try {
            smokingLogService.checkMissingLogs();
            log.info("Completed scheduled task to check for missing smoking logs");
        } catch (Exception e) {
            log.error("Error in scheduled task to check for missing smoking logs", e);
        }
    }
}
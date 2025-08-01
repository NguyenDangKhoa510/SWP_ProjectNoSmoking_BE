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


     // Gui tb hang ngày cho người dùng chưa ghi nhật ký hút thuốc trong ngày.

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
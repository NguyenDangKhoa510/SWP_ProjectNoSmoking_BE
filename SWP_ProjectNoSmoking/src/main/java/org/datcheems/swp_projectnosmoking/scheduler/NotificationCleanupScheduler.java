package org.datcheems.swp_projectnosmoking.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.repository.UserNotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final UserNotificationRepository userNotificationRepository;

    /**
     * Xóa các thông báo cũ hơn 3 ngày. Chạy mỗi ngày lúc 03:00 sáng.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteOldUserNotifications() {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(3);
            int count = userNotificationRepository.findBySentAtBefore(threshold).size();
            userNotificationRepository.deleteAllOlderThan(threshold);
            log.info("✅ Đã xóa {} thông báo cũ (gửi trước {}).", count, threshold);
        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa thông báo cũ: ", e);
        }
    }
}

package org.datcheems.swp_projectnosmoking.repository;

import jakarta.transaction.Transactional;
import org.datcheems.swp_projectnosmoking.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    List<UserNotification> findByUserId(Long userId);
    @Transactional
    @Modifying
    @Query("DELETE FROM UserNotification u WHERE u.notification.notificationId = :notificationId")
    void deleteByNotificationId(Long notificationId);

    List<UserNotification> findByNotification_CreatedBy_Id(Long coachUserId);


}






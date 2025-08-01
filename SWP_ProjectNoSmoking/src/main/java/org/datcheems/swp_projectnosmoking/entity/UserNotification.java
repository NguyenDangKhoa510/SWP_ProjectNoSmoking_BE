package org.datcheems.swp_projectnosmoking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_notifications")
@Data
@NoArgsConstructor
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNotificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus = DeliveryStatus.SENT;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String personalizedReason;

    public enum DeliveryStatus {
        SENT,
        FAILED
    }

    @PrePersist
    public void prePersist() {
        sentAt = LocalDateTime.now();
    }
    @Column(name = "is_read")
    private Boolean isRead = false;

}


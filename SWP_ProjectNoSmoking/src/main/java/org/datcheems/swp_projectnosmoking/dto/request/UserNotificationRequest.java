package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class UserNotificationRequest {
    private Long userId;
    private Long notificationId;
    private String personalizedReason;
}
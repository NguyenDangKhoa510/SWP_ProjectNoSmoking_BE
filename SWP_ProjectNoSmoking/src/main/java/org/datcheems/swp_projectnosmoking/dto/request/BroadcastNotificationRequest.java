package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class BroadcastNotificationRequest {
    private Long notificationId;
    private String personalizedReason;
}

package org.datcheems.swp_projectnosmoking.dto.response;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserNotificationResponse {
    private Boolean hasBeenRead;
    private Long userNotificationId;
    private String notificationTitle;
    private String content;
    private String personalizedReason;
    private String deliveryStatus;
    private LocalDateTime sentAt;
}

package org.datcheems.swp_projectnosmoking.dto.request;


import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String content;
    private Boolean isActive = true;

}

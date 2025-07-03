package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageRestResponse {
    private Long messageId;
    private String senderType;
    private String content;
    private LocalDateTime sentAt;
}

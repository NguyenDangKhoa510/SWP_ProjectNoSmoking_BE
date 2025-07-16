package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class MessageSendRequest {
    private Long selectionId;
    private String content;
    private String senderType;
    private Long userId;
    private Long coachId;
}

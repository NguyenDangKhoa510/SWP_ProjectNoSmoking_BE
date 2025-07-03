package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class MessageRequest {
    private Long receiverId;
    private String message;
}


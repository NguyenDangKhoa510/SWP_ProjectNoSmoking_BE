package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

@Data
public class ResponseObject {
    private String status;
    private String message;
    private Object data;
}

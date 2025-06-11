package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

@Data
public class ResponseObject<T> {
    private String status;
    private String message;
    private T data;
}


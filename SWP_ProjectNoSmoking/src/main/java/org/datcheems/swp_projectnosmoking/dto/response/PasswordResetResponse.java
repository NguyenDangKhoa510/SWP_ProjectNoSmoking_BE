package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

@Data
public class PasswordResetResponse {
    private String code;
    private String newPassword;
}

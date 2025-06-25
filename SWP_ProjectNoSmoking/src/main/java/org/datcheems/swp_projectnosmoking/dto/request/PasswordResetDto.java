package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String code;
    private String newPassword;
}

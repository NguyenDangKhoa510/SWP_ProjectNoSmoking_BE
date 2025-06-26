package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileResponse {
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String address;
    private String gender;
    private String avatarUrl;
}

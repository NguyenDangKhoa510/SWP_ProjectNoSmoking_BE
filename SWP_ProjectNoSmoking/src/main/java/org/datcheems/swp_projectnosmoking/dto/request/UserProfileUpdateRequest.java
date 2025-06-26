package org.datcheems.swp_projectnosmoking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateRequest {
    @NotBlank
    @Size(min = 1, max = 40)
    private String fullName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String address;
    private String gender;
    private String avatarUrl;
}

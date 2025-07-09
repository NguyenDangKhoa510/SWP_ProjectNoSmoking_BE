package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter

public class MemberProfileResponse {
    private Long memberId;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private LocalDate birthDate;
    private String address;
    private String gender;
    private LocalDateTime createdAt;

}


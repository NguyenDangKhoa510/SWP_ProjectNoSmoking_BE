package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

@Data
public class CoachProfileResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String specialization;
    private String bio;
    private Double rating;
    private Integer yearsOfExperience;
    private String imageUrl;
    private Integer maxMembers;
}

package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class CoachProfileUpdateRequest {
    private String fullName;
    private String specialization;
    private String bio;
    private Integer yearsOfExperience;
    private String imageUrl;
}

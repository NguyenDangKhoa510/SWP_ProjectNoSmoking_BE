package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanRequest {
    private String memberIdentifier;  // Username or email of the member
    private LocalDate startDate;
    private String goal;    // optional
}

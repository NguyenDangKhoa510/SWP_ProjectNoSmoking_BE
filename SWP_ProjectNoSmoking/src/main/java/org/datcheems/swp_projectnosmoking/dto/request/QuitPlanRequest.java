package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanRequest {
    private String memberIdentifier;
    private String coachIdentifier;
    private String reasonToQuit;
    private Integer totalStages;
    private String goal;
    private String status;
}

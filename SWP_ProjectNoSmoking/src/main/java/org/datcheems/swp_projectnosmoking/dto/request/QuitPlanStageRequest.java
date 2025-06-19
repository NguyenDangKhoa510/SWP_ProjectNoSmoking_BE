package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanStageRequest {
    private Integer quitPlanId;
    private Integer stageNumber;
    private LocalDate milestoneDate;
    private String advice;
}

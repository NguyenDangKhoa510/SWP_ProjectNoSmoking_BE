package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanStageResponse {
    private Integer id;
    private Integer quitPlanId;
    private Integer stageNumber;
    private LocalDate milestoneDate;
    private String status;
    private String advice;
}

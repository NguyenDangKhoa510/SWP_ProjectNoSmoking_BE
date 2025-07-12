package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanStageResponse {
    private Long stageId;
    private Integer stageNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetCigaretteCount;
    private String advice;
    private String status;
    private Double progressPercentage;
}

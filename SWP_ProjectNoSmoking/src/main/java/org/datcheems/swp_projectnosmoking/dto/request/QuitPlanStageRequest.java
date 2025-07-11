package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanStageRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetCigaretteCount;
    private String advice;
}
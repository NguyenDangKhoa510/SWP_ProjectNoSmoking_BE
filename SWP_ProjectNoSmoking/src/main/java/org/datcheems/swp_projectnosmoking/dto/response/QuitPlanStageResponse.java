package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanStageResponse {
    private Long stageId;
    private int day;
    private String description;
    private LocalDate targetDate;
}

package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuitPlanStageRequest {
    private int day;
    private String description;
    private LocalDate targetDate;
}
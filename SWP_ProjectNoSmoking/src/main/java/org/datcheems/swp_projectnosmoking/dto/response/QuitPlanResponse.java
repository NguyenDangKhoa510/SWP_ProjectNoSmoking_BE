package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class QuitPlanResponse {
    private Long quitPlanId;
    private Long memberId;
    private Long coachId;
    private String reasonToQuit;
    private Integer totalStages;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String goal;
    private List<QuitPlanStageResponse> stages;
}

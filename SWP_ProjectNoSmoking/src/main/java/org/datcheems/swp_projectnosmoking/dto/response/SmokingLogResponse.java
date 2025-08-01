package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;
import org.datcheems.swp_projectnosmoking.entity.SmokingLog;

import java.time.LocalDate;

@Data
public class SmokingLogResponse {
    private Long logId;
    private Long userId;
    private String memberName;
    private Boolean smoked;
    private Integer smokeCount;
    private SmokingLog.CravingLevel cravingLevel;
    private String healthStatus;
    private LocalDate logDate;
    private SmokingLog.Frequency frequency;
    private Integer previousSmokeCount; // For comparison with previous log
    private Boolean isImprovement; // True if smoke count decreased or is zero
    private Long quitPlanStageId;
    private Integer stageNumber;
}
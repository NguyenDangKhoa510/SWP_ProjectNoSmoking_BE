package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

@Data
public class QuitPlanRequest {
    private Integer memberId;
    private Integer coachId;
    private Integer selectionId;
    private String goalDescription;
}

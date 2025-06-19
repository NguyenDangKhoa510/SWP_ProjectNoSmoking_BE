package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuitPlanResponse {
    private Integer id;
    private Integer memberId;
    private Integer coachId;
    private Integer selectionId;
    private String goalDescription;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

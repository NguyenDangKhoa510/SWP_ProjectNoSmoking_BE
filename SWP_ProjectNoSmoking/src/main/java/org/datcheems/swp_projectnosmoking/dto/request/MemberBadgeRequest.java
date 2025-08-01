package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberBadgeRequest {
    private Long memberId;
    private long badgeId;
    private LocalDate awardedDate;
}

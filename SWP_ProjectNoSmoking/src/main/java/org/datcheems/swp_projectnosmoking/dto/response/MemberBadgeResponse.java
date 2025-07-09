package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MemberBadgeResponse {
    private Long id;
    private String badgeName;
    private String badgeDescription;
    private int score;
    private LocalDate awardedDate;
}

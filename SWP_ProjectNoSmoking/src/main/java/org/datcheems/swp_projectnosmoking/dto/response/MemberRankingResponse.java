package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

@Data
public class MemberRankingResponse {
    private Long memberId;
    private String fullName;
    private String email;
    private String avatarUrl;
    private int totalScore;
}

package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInitialInfoResponse {
    private Long memberId;
    private String fullName;
    private Integer yearsSmoking;
    private Integer cigarettesPerDay;
    private String reasonToQuit;
    private String healthStatus;
}

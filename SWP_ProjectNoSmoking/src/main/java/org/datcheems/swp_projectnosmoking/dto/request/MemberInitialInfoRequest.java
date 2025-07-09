package org.datcheems.swp_projectnosmoking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInitialInfoRequest {
    @NotNull
    private Integer yearsSmoking;

    @NotNull
    private Integer cigarettesPerDay;

    @NotBlank
    private String reasonToQuit;

    @NotBlank
    private String healthStatus;
}

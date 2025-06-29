package org.datcheems.swp_projectnosmoking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.datcheems.swp_projectnosmoking.entity.SmokingLog;

import java.time.LocalDate;

@Data
public class SmokingLogRequest {

    @NotNull(message = "Smoke count is required")
    @Min(value = 0, message = "Smoke count must be a positive number")
    private Integer smokeCount;

    private LocalDate logDate;

    @NotNull(message = "Frequency is required")
    private SmokingLog.Frequency frequency;
}
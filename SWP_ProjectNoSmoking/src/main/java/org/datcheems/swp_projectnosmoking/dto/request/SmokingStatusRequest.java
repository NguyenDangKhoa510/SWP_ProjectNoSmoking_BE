package org.datcheems.swp_projectnosmoking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SmokingStatusRequest {
    private int cigarettesPerDay;
    private String frequency;
    private double pricePerPack;
    private LocalDate recordDate;
}

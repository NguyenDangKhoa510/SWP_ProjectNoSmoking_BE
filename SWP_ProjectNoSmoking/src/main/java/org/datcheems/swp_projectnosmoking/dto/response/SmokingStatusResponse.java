package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SmokingStatusResponse {
    private int cigarettesPerDay;
    private String frequency;
    private double pricePerPack;
    private LocalDate recordDate;
}


package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatisticsResponse {
    private Double totalRevenue;
    private Integer totalTransactions;
    private YearMonth period; // For monthly statistics
    private Integer year; // For yearly statistics
}
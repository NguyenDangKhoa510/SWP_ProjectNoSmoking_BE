package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueChartResponse {
    private List<MonthlyRevenueData> monthlyData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenueData {
        private String month; // Format: "YYYY-MM" or "Month YYYY"
        private Double revenue;
        private Integer transactions;
    }
}
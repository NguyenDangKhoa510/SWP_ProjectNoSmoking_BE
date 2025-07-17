package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.MonthlyRevenueChartResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.RevenueStatisticsResponse;
import org.datcheems.swp_projectnosmoking.service.RevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;

    /**
     * Get revenue statistics for the current month
     * @return ResponseEntity with revenue statistics
     */
    @GetMapping("/current-month")
    public ResponseEntity<ResponseObject<RevenueStatisticsResponse>> getCurrentMonthRevenue() {
        RevenueStatisticsResponse statistics = revenueService.getRevenueForCurrentMonth();
        
        ResponseObject<RevenueStatisticsResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Current month revenue statistics fetched successfully");
        response.setData(statistics);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get revenue statistics for a specific month
     * @param year Year
     * @param month Month (1-12)
     * @return ResponseEntity with revenue statistics
     */
    @GetMapping("/month")
    public ResponseEntity<ResponseObject<RevenueStatisticsResponse>> getMonthRevenue(
            @RequestParam int year,
            @RequestParam int month) {
        
        RevenueStatisticsResponse statistics = revenueService.getRevenueForMonth(year, month);
        
        ResponseObject<RevenueStatisticsResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Monthly revenue statistics fetched successfully");
        response.setData(statistics);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get revenue statistics for the current year
     * @return ResponseEntity with revenue statistics
     */
    @GetMapping("/current-year")
    public ResponseEntity<ResponseObject<RevenueStatisticsResponse>> getCurrentYearRevenue() {
        RevenueStatisticsResponse statistics = revenueService.getRevenueForCurrentYear();
        
        ResponseObject<RevenueStatisticsResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Current year revenue statistics fetched successfully");
        response.setData(statistics);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get revenue statistics for a specific year
     * @param year Year
     * @return ResponseEntity with revenue statistics
     */
    @GetMapping("/year")
    public ResponseEntity<ResponseObject<RevenueStatisticsResponse>> getYearRevenue(
            @RequestParam int year) {
        
        RevenueStatisticsResponse statistics = revenueService.getRevenueForYear(year);
        
        ResponseObject<RevenueStatisticsResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Yearly revenue statistics fetched successfully");
        response.setData(statistics);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get monthly revenue data for chart visualization
     * @param months Number of months to include (default: 12)
     * @return ResponseEntity with monthly revenue data
     */
    @GetMapping("/chart")
    public ResponseEntity<ResponseObject<MonthlyRevenueChartResponse>> getRevenueChart(
            @RequestParam(defaultValue = "12") int months) {
        
        MonthlyRevenueChartResponse chartData = revenueService.getMonthlyRevenueChart(months);
        
        ResponseObject<MonthlyRevenueChartResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Monthly revenue chart data fetched successfully");
        response.setData(chartData);
        
        return ResponseEntity.ok(response);
    }
}
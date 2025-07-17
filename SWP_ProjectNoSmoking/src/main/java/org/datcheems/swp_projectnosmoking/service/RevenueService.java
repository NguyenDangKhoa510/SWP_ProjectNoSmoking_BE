package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.response.MonthlyRevenueChartResponse;
import org.datcheems.swp_projectnosmoking.dto.response.RevenueStatisticsResponse;
import org.datcheems.swp_projectnosmoking.entity.UserMembership;
import org.datcheems.swp_projectnosmoking.repository.UserMembershipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueService {

    private final UserMembershipRepository userMembershipRepository;

    /**
     * Get revenue statistics for the most recent month
     * @return RevenueStatisticsResponse containing total revenue and transaction count
     */
    public RevenueStatisticsResponse getRevenueForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        return getRevenueForMonth(currentMonth.getYear(), currentMonth.getMonthValue());
    }

    /**
     * Get revenue statistics for a specific month
     * @param year Year
     * @param month Month (1-12)
     * @return RevenueStatisticsResponse containing total revenue and transaction count
     */
    public RevenueStatisticsResponse getRevenueForMonth(int year, int month) {
        List<UserMembership> memberships = userMembershipRepository.findByStartDateYearAndMonth(year, month);
        
        double totalRevenue = calculateTotalRevenue(memberships);
        
        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(memberships.size())
                .period(YearMonth.of(year, month))
                .build();
    }

    /**
     * Get revenue statistics for the current year
     * @return RevenueStatisticsResponse containing total revenue and transaction count
     */
    public RevenueStatisticsResponse getRevenueForCurrentYear() {
        int currentYear = LocalDate.now().getYear();
        return getRevenueForYear(currentYear);
    }

    /**
     * Get revenue statistics for a specific year
     * @param year Year
     * @return RevenueStatisticsResponse containing total revenue and transaction count
     */
    public RevenueStatisticsResponse getRevenueForYear(int year) {
        List<UserMembership> memberships = userMembershipRepository.findByStartDateYear(year);
        
        double totalRevenue = calculateTotalRevenue(memberships);
        
        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(memberships.size())
                .year(year)
                .build();
    }

    /**
     * Get monthly revenue data for chart visualization
     * @param months Number of months to include (default: 12)
     * @return MonthlyRevenueChartResponse containing monthly revenue data
     */
    public MonthlyRevenueChartResponse getMonthlyRevenueChart(int months) {
        YearMonth currentMonth = YearMonth.now();
        List<MonthlyRevenueChartResponse.MonthlyRevenueData> monthlyData = new ArrayList<>();
        
        // Get data for each month, starting from current month and going back
        for (int i = 0; i < months; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            List<UserMembership> memberships = userMembershipRepository.findByStartDateYearAndMonth(
                    targetMonth.getYear(), 
                    targetMonth.getMonthValue()
            );
            
            double revenue = calculateTotalRevenue(memberships);
            
            MonthlyRevenueChartResponse.MonthlyRevenueData data = MonthlyRevenueChartResponse.MonthlyRevenueData.builder()
                    .month(targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .revenue(revenue)
                    .transactions(memberships.size())
                    .build();
            
            monthlyData.add(data);
        }
        
        // Reverse the list so it's in chronological order
        List<MonthlyRevenueChartResponse.MonthlyRevenueData> chronologicalData = 
                monthlyData.stream()
                        .sorted((d1, d2) -> d1.getMonth().compareTo(d2.getMonth()))
                        .collect(Collectors.toList());
        
        return MonthlyRevenueChartResponse.builder()
                .monthlyData(chronologicalData)
                .build();
    }
    
    /**
     * Calculate total revenue from a list of UserMembership objects
     * @param memberships List of UserMembership objects
     * @return Total revenue
     */
    private double calculateTotalRevenue(List<UserMembership> memberships) {
        return memberships.stream()
                .mapToDouble(membership -> {
                    if (membership.getMembershipPackageId() != null && 
                        membership.getMembershipPackageId().getPrice() != null) {
                        return membership.getMembershipPackageId().getPrice();
                    }
                    return 0.0;
                })
                .sum();
    }
}
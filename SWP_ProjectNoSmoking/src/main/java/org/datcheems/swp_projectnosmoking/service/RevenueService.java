package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.response.MonthlyRevenueChartResponse;
import org.datcheems.swp_projectnosmoking.dto.response.RevenueStatisticsResponse;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.UserMembership;
import org.datcheems.swp_projectnosmoking.repository.MemberCoachSelectionRepository;
import org.datcheems.swp_projectnosmoking.repository.UserMembershipRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final MemberCoachSelectionRepository  memberCoachSelectionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public RevenueStatisticsResponse getRevenueForCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        return getRevenueForMonth(currentMonth.getYear(), currentMonth.getMonthValue());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RevenueStatisticsResponse getRevenueForMonth(int year, int month) {
        List<UserMembership> memberships = userMembershipRepository.findByStartDateYearAndMonth(year, month);
        
        double totalRevenue = calculateTotalRevenue(memberships)*0.3;
        
        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(memberships.size())
                .period(YearMonth.of(year, month))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RevenueStatisticsResponse getRevenueForCurrentYear() {
        int currentYear = LocalDate.now().getYear();
        return getRevenueForYear(currentYear);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RevenueStatisticsResponse getRevenueForYear(int year) {
        List<UserMembership> memberships = userMembershipRepository.findByStartDateYear(year);

        double totalRevenue = calculateTotalRevenue(memberships)*0.3;
        
        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(memberships.size())
                .year(year)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MonthlyRevenueChartResponse getMonthlyRevenueChart(int months) {
        YearMonth currentMonth = YearMonth.now();
        List<MonthlyRevenueChartResponse.MonthlyRevenueData> monthlyData = new ArrayList<>();
        
        //Get data for each month, starting from current month and going back
        for (int i = 0; i < months; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            List<UserMembership> memberships = userMembershipRepository.findByStartDateYearAndMonth(
                    targetMonth.getYear(), 
                    targetMonth.getMonthValue()
            );
            
            double revenue = calculateTotalRevenue(memberships)*0.3;
            
            MonthlyRevenueChartResponse.MonthlyRevenueData data = MonthlyRevenueChartResponse.MonthlyRevenueData.builder()
                    .month(targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .revenue(revenue)
                    .transactions(memberships.size())
                    .build();
            
            monthlyData.add(data);
        }
        
        //Reverse the list so it's in chronological order
        List<MonthlyRevenueChartResponse.MonthlyRevenueData> chronologicalData = 
                monthlyData.stream()
                        .sorted((d1, d2) -> d1.getMonth().compareTo(d2.getMonth()))
                        .collect(Collectors.toList());
        
        return MonthlyRevenueChartResponse.builder()
                .monthlyData(chronologicalData)
                .build();
    }

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

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public RevenueStatisticsResponse getRevenueOfCoachForMonth(int year, int month, long coachId) {
        //Lấy danh sách membership trong tháng
        List<UserMembership> membershipsInMonth =
                userMembershipRepository.findByStartDateYearAndMonth(year, month);

        //Lấy danh sách member đã chọn coach đó
        List<MemberCoachSelection> coachSelections =
                memberCoachSelectionRepository.findByCoachId(coachId);
        List<Long> memberIdsOfCoach = coachSelections.stream()
                .map(selection -> selection.getMember().getUserId())
                .toList();

        //Lọc các membership thuộc về các member đó
        List<UserMembership> membershipsOfCoach = membershipsInMonth.stream()
                .filter(m -> m.getMember() != null &&
                        memberIdsOfCoach.contains(m.getMember().getUserId()))
                .toList();

        //tính tổng doanh thu
        double totalRevenue = calculateTotalRevenue(membershipsOfCoach)*0.7;

        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(membershipsOfCoach.size())
                .period(YearMonth.of(year, month))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public RevenueStatisticsResponse getRevenueOfCoachForCurrentMonth(long coachId) {
        YearMonth currentMonth = YearMonth.now();
        return getRevenueOfCoachForMonth(currentMonth.getYear(), currentMonth.getMonthValue(), coachId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public RevenueStatisticsResponse getRevenueOfCoachForYear(int year, long coachId) {
        //Lấy tất cả user membership trong năm đó
        List<UserMembership> membershipsInYear = userMembershipRepository.findByStartDateYear(year);

        //Lấy danh sách member đã chọn coach tương ứng
        List<MemberCoachSelection> coachSelections =
                memberCoachSelectionRepository.findByCoachId(coachId);
        List<Long> memberIdsOfCoach = coachSelections.stream()
                .map(selection -> selection.getMember().getUserId())
                .toList();

        //Lọc các membership thuộc về các member đó
        List<UserMembership> membershipsOfCoach = membershipsInYear.stream()
                .filter(m -> m.getMember() != null &&
                        memberIdsOfCoach.contains(m.getMember().getUserId()))
                .toList();

        //Tính tổng doanh thu
        double totalRevenue = calculateTotalRevenue(membershipsOfCoach)*0.7;

        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(membershipsOfCoach.size())
                .year(year)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public RevenueStatisticsResponse getRevenueOfCoachForCurrentYear(long coachId) {
        int currentYear = LocalDate.now().getYear();
        return getRevenueOfCoachForYear(currentYear, coachId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public MonthlyRevenueChartResponse getMonthlyRevenueChartForCoach(int months, long coachId) {
        YearMonth currentMonth = YearMonth.now();
        List<MonthlyRevenueChartResponse.MonthlyRevenueData> monthlyData = new ArrayList<>();


        List<MemberCoachSelection> coachSelections = memberCoachSelectionRepository.findByCoachId(coachId);
        List<Long> memberIdsOfCoach = coachSelections.stream()
                .map(selection -> selection.getMember().getUserId())  // hoặc getId() nếu ID là memberId
                .toList();


        for (int i = 0; i < months; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);


            List<UserMembership> memberships = userMembershipRepository.findByStartDateYearAndMonth(
                    targetMonth.getYear(),
                    targetMonth.getMonthValue()
            );


            List<UserMembership> coachMemberships = memberships.stream()
                    .filter(m -> m.getMember() != null &&
                            memberIdsOfCoach.contains(m.getMember().getUserId()))
                    .toList();


            double revenue = calculateTotalRevenue(coachMemberships) * 0.7;

            MonthlyRevenueChartResponse.MonthlyRevenueData data =
                    MonthlyRevenueChartResponse.MonthlyRevenueData.builder()
                            .month(targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                            .revenue(revenue)
                            .transactions(coachMemberships.size())
                            .build();

            monthlyData.add(data);
        }


        List<MonthlyRevenueChartResponse.MonthlyRevenueData> chronologicalData = monthlyData.stream()
                .sorted((d1, d2) -> d1.getMonth().compareTo(d2.getMonth()))
                .collect(Collectors.toList());

        return MonthlyRevenueChartResponse.builder()
                .monthlyData(chronologicalData)
                .build();
    }

}
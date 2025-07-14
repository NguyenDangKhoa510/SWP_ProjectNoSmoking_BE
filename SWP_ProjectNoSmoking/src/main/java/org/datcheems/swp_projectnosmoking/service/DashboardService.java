package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.DashboardResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final CoachRepository coachRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final NotificationRepository notificationRepository;
    private final SmokingLogRepository smokingLogRepository;
    private final CoachReviewRepository coachReviewRepository;

    public DashboardResponse getAdvancedDashboardStats() {
        DashboardResponse response = new DashboardResponse();

        response.setTotalUsers(userRepository.count());
        response.setTotalMembers(memberRepository.count());
        response.setTotalCoaches(coachRepository.count());
        response.setTotalQuitPlans(quitPlanRepository.count());
        response.setTotalNotifications(notificationRepository.count());

        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfCurrentMonth = currentMonth.atDay(1);
        LocalDate startOfPreviousMonth = currentMonth.minusMonths(1).atDay(1);
        LocalDate endOfPreviousMonth = startOfCurrentMonth.minusDays(1);

        long currentMonthUsers = userRepository.countByCreatedAtBetween(
                startOfCurrentMonth.atStartOfDay(),
                LocalDateTime.now()   // Lấy đúng thời gian hiện tại
        );

        long previousMonthUsers = userRepository.countByCreatedAtBetween(
                startOfPreviousMonth.atStartOfDay(),
                endOfPreviousMonth.atTime(23, 59, 59)  // Lấy đến cuối ngày
        );


        response.setNewUsersThisMonth(currentMonthUsers);

        double growthRate = previousMonthUsers == 0 ? 0
                : ((double)(currentMonthUsers - previousMonthUsers) / previousMonthUsers) * 100;
        response.setGrowthRatePercent(Math.round(growthRate * 100.0) / 100.0);

        // Top 5 members hút ít nhất
        List<Object[]> topMembers = smokingLogRepository.findTop5MembersWithLeastSmoking(startOfCurrentMonth, LocalDate.now());

        List<Map<String, Object>> namesWithCount = topMembers.stream()
                .map(record -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", record[1]);
                    map.put("totalSmoke", record[2]);
                    return map;
                })
                .collect(Collectors.toList());

        response.setTopMembersWithSmokeCount(namesWithCount);

        List<Object[]> topCoachesRaw = coachReviewRepository.findTopRatedCoaches(PageRequest.of(0, 5));

        List<Map<String, Object>> topCoaches = topCoachesRaw.stream()
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("coachId", obj[0]);
                    map.put("coachName", obj[1]);
                    map.put("averageRating", Math.round((Double)obj[2] * 100.0) / 100.0);
                    return map;
                })
                .collect(Collectors.toList());

        response.setTopRatedCoaches(topCoaches);



        return response;
    }



}

package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardResponse {
    private long totalUsers;
    private long totalMembers;
    private long totalCoaches;
    private long totalQuitPlans;
    private long totalNotifications;
    private long newUsersThisMonth;
    private double growthRatePercent;
    private List<Map<String, Object>> topMembersWithSmokeCount;
    private List<Map<String, Object>> topRatedCoaches;


}

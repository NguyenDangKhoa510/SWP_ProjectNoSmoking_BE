package org.datcheems.swp_projectnosmoking.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoachDashboardResponse {
    private long totalMembers;
    private long totalQuitPlans;
    private long totalReviews;
    private double averageRating;
}

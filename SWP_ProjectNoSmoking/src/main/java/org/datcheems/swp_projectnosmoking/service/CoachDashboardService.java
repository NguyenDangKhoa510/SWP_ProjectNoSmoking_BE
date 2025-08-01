package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.CoachDashboardResponse;
import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoachDashboardService {

    private final CoachRepository coachRepository;
    private final MemberCoachSelectionRepository memberCoachSelectionRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final CoachReviewRepository coachReviewRepository;
    private final UserRepository userRepository;

    public CoachDashboardResponse getCoachDashboardStats() {
        User currentUser = getCurrentUser();

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        long totalMembers = memberCoachSelectionRepository.countByCoach(coach);
        long totalQuitPlans = quitPlanRepository.countByCoach(coach);
        long totalReviews = coachReviewRepository.countByCoach(coach);

        Double avgRating = coachReviewRepository.findAverageRatingByCoachId(coach.getUserId());
        double averageRating = avgRating != null ? avgRating : 0.0;

        CoachDashboardResponse response = new CoachDashboardResponse();
        response.setTotalMembers(totalMembers);
        response.setTotalQuitPlans(totalQuitPlans);
        response.setTotalReviews(totalReviews);
        response.setAverageRating(Math.round(averageRating * 100.0) / 100.0);

        return response;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

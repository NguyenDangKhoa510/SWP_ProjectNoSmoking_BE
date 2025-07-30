package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.CoachReviewRequest;
import org.datcheems.swp_projectnosmoking.dto.response.CoachReviewResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachReviewService {

    private final UserRepository userRepository;
    private final CoachRepository coachRepository;
    private final MemberRepository memberRepository;
    private final CoachReviewRepository coachReviewRepository;
    private final MemberCoachSelectionRepository memberCoachSelectionRepository;


    public CoachReviewResponse createReview(CoachReviewRequest request) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "MEMBER")) {
            throw new RuntimeException("Only members can review coaches");
        }

        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Coach coach = coachRepository.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));


        boolean hasWorkedTogether = memberCoachSelectionRepository.existsByMemberAndCoach(member, coach);
        if (!hasWorkedTogether) {
            throw new RuntimeException("You cannot review a coach you have never worked with");
        }

        if (coachReviewRepository.existsByCoachAndMember(coach, member)) {
            throw new RuntimeException("You have already reviewed this coach");
        }

        CoachReview review = new CoachReview();
        review.setCoach(coach);
        review.setMember(member);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CoachReview saved = coachReviewRepository.save(review);

        return toResponse(saved);
    }


    public List<CoachReviewResponse> getReviewsForCurrentCoach() {
        User currentUser = getCurrentUser();

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        List<CoachReview> reviews = coachReviewRepository.findByCoach(coach);

        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CoachReviewResponse updateReview(Long reviewId, CoachReviewRequest request) {
        User currentUser = getCurrentUser();

        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        CoachReview review = coachReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getMember().getUserId().equals(member.getUserId())) {
            throw new RuntimeException("You can only edit your own review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CoachReview updated = coachReviewRepository.save(review);
        return toResponse(updated);
    }


    public Map<String, Object> getReviewStatisticsForCurrentCoach() {
        User currentUser = getCurrentUser();

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        List<CoachReview> reviews = coachReviewRepository.findByCoach(coach);

        double average = reviews.stream()
                .mapToInt(CoachReview::getRating)
                .average()
                .orElse(0);

        int totalReviews = reviews.size();

        return Map.of(
                "totalReviews", totalReviews,
                "averageRating", average
        );
    }

    public List<CoachReviewResponse> getReviewsByCurrentMember() {
        User currentUser = getCurrentUser();

        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Member profile not found"));

        List<CoachReview> reviews = coachReviewRepository.findByMember(member);

        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public List<CoachReviewResponse> getAllReviews() {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new RuntimeException("Only admin can access all reviews");
        }

        List<CoachReview> reviews = coachReviewRepository.findAll();

        return reviews.stream()
                .map(review -> toResponse(review, true))
                .collect(Collectors.toList());
    }



    public Map<String, Object> getReviewStatisticsByCoachId(Long coachId) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new RuntimeException("Only admin can access this statistic");
        }

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        List<CoachReview> reviews = coachReviewRepository.findByCoach(coach);

        double average = reviews.stream()
                .mapToInt(CoachReview::getRating)
                .average()
                .orElse(0);

        int totalReviews = reviews.size();

        return Map.of(
                "totalReviews", totalReviews,
                "averageRating", average
        );
    }


    public void deleteReviewByAdmin(Long reviewId) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new RuntimeException("Only admin can delete reviews");
        }

        CoachReview review = coachReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        coachReviewRepository.delete(review);
    }




    public List<CoachReviewResponse> getReviewsByCoachId(Long coachId) {
        User currentUser = getCurrentUser();

        if (!hasRole(currentUser, "ADMIN")) {
            throw new RuntimeException("Only admin can view reviews by coach");
        }

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        List<CoachReview> reviews = coachReviewRepository.findByCoach(coach);

        return reviews.stream()
                .map(review -> toResponse(review, true))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getPublicReviewsAndStatsByCoachId(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        List<CoachReview> reviews = coachReviewRepository.findByCoach(coach);

        double averageRating = reviews.stream()
                .mapToInt(CoachReview::getRating)
                .average()
                .orElse(0.0);

        int totalReviews = reviews.size();

        List<CoachReviewResponse> reviewResponses = reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return Map.of(
                "averageRating", averageRating,
                "totalReviews", totalReviews,
                "reviews", reviewResponses
        );
    }

    private CoachReviewResponse toResponse(CoachReview review, boolean isAdminView) {
        CoachReviewResponse res = new CoachReviewResponse();
        res.setReviewId(review.getId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setCreatedAt(review.getCreatedAt());

        if (isAdminView) {
            res.setReviewerName(review.getMember().getUser().getFullName());
            res.setReviewerUsername(review.getMember().getUser().getUsername());
        } else {
            res.setReviewerName("Ẩn danh");
            res.setReviewerUsername(null);
        }

        return res;
    }

    private CoachReviewResponse toResponse(CoachReview review) {
        return toResponse(review, false);
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equalsIgnoreCase(roleName));
    }
}
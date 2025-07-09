package org.datcheems.swp_projectnosmoking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.CoachReviewRequest;
import org.datcheems.swp_projectnosmoking.dto.response.CoachReviewResponse;
import org.datcheems.swp_projectnosmoking.service.CoachReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coach-reviews")
@RequiredArgsConstructor
public class CoachReviewController {

    private final CoachReviewService coachReviewService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<CoachReviewResponse>> getAllReviews() {
        List<CoachReviewResponse> reviews = coachReviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<CoachReviewResponse>> getReviewsByCoachId(@PathVariable Long coachId) {
        List<CoachReviewResponse> reviews = coachReviewService.getReviewsByCoachId(coachId);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/statistics/{coachId}")
    public ResponseEntity<Map<String, Object>> getStatisticsByCoachId(@PathVariable Long coachId) {
        return ResponseEntity.ok(coachReviewService.getReviewStatisticsByCoachId(coachId));
    }




    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CoachReviewRequest request) {
        CoachReviewResponse response = coachReviewService.createReview(request);
        return ResponseEntity.ok(Map.of(
                "message", "Review submitted successfully",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody @Valid CoachReviewRequest request) {
        CoachReviewResponse response = coachReviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Review updated successfully",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my-reviews-member")
    public ResponseEntity<?> getMyReviewHistory() {
        List<CoachReviewResponse> responses = coachReviewService.getReviewsByCurrentMember();
        return ResponseEntity.ok(Map.of(
                "message", "Your coach reviews retrieved successfully",
                "data", responses
        ));
    }




    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    @GetMapping("/my-reviews-coach")
    public ResponseEntity<?> getCoachReviews() {
        List<CoachReviewResponse> responses = coachReviewService.getReviewsForCurrentCoach();
        return ResponseEntity.ok(Map.of(
                "message", "Your coach reviews retrieved successfully",
                "data", responses
        ));
    }

    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    @GetMapping("/me/statistics")
    public ResponseEntity<?> getMyReviewStatistics() {
        Map<String, Object> stats = coachReviewService.getReviewStatisticsForCurrentCoach();
        return ResponseEntity.ok(Map.of(
                "message", "Coach review statistics retrieved successfully",
                "data", stats
        ));
    }


}

package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.CoachDashboardResponse;
import org.datcheems.swp_projectnosmoking.service.CoachDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/coach-dashboard")
@RequiredArgsConstructor
public class CoachDashboardController {

    private final CoachDashboardService coachDashboardService;

    @PreAuthorize("hasRole('COACH')")
    @GetMapping
    public ResponseEntity<?> getCoachDashboard() {
        CoachDashboardResponse stats = coachDashboardService.getCoachDashboardStats();
        return ResponseEntity.ok(Map.of(
                "message", "Coach dashboard data retrieved successfully",
                "data", stats
        ));
    }
}

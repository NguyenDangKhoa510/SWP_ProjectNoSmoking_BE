package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanRequest;
import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanStageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanResponse;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanStageResponse;
import org.datcheems.swp_projectnosmoking.service.QuitPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quitplan")
@RequiredArgsConstructor
public class QuitPlanController {

    private final QuitPlanService quitPlanService;

    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/create")
    public ResponseEntity<?> createQuitPlan(@RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = quitPlanService.createQuitPlan(request);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan created successfully",
                "data", response
        ));
    }

    @PreAuthorize("hasAnyRole('COACH', 'MEMBER')")
    @GetMapping("/{quitPlanId}")
    public ResponseEntity<?> getQuitPlanById(@PathVariable Long quitPlanId) {
        QuitPlanResponse response = quitPlanService.getQuitPlanById(quitPlanId);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan details fetched successfully",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/member")
    public ResponseEntity<?> getQuitPlansForCurrentMember() {
        List<QuitPlanResponse> responses = quitPlanService.getQuitPlansForCurrentMember();
        return ResponseEntity.ok(Map.of(
                "message", "Quit plans for member retrieved successfully",
                "data", responses
        ));
    }

    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/coach")
    public ResponseEntity<?> getQuitPlansForCurrentCoach() {
        List<QuitPlanResponse> responses = quitPlanService.getQuitPlansForCurrentCoach();
        return ResponseEntity.ok(Map.of(
                "message", "Quit plans for coach retrieved successfully",
                "data", responses
        ));
    }

    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/{quitPlanId}/stage")
    public ResponseEntity<?> addQuitPlanStage(@PathVariable Long quitPlanId, @RequestBody QuitPlanStageRequest request) {
        QuitPlanStageResponse response = quitPlanService.addQuitPlanStage(quitPlanId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan stage added successfully",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('COACH')")
    @PutMapping("/stage/{stageId}")
    public ResponseEntity<?> updateQuitPlanStage(@PathVariable Long stageId, @RequestBody QuitPlanStageRequest request) {
        QuitPlanStageResponse response = quitPlanService.updateQuitPlanStage(stageId, request);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan stage updated successfully",
                "data", response
        ));
    }

    @PreAuthorize("hasRole('COACH')")
    @DeleteMapping("/stage/{stageId}")
    public ResponseEntity<?> deleteQuitPlanStage(@PathVariable Long stageId) {
        quitPlanService.deleteQuitPlanStage(stageId);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan stage deleted successfully"
        ));
    }

    @GetMapping("/stage/{stageId}")
    public ResponseEntity<?> getQuitPlanStageById(@PathVariable Long stageId) {
        QuitPlanStageResponse response = quitPlanService.getQuitPlanStageById(stageId);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan stage details fetched successfully",
                "data", response
        ));
    }

}

package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanRequest;
import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanStageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanResponse;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanStageResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.service.QuitPlanService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quitplan")
@RequiredArgsConstructor
public class QuitPlanController {

    private final QuitPlanService quitPlanService;
    private final UserRepository userRepository;


    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/create")
    public ResponseEntity<?> createQuitPlan(@RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = quitPlanService.createQuitPlan(request);
        return ResponseEntity.ok(Map.of(
                "message", "Quit plan created successfully",
                "data", response
        ));
    }

//    @PreAuthorize("hasAnyRole('COACH', 'MEMBER')")
//    @GetMapping("/{quitPlanId}")
//    public ResponseEntity<?> getQuitPlanById(@PathVariable Long quitPlanId) {
//        QuitPlanResponse response = quitPlanService.getQuitPlanById(quitPlanId);
//        return ResponseEntity.ok(Map.of(
//                "message", "Quit plan details fetched successfully",
//                "data", response
//        ));
//    }

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

    @GetMapping("/stages/my")
    public ResponseEntity<List<QuitPlanStageResponse>> getAllStagesForCurrentUser() {
        List<QuitPlanStageResponse> stages = quitPlanService.getAllQuitPlanStagesForCurrentUser();
        return ResponseEntity.ok(stages);
    }

    @PostMapping("/quit-plan-stages/{stageId}/retry-request")
    public ResponseEntity<String> requestRetryStage(@PathVariable Long stageId, @AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        quitPlanService.sendRetryStageRequestToCoach(stageId, username);
        return ResponseEntity.ok("Yêu cầu làm lại stage đã được gửi đến huấn luyện viên.");
    }

    @PostMapping("/coach/reset-stage/{stageId}")
    public ResponseEntity<ResponseObject<Void>> resetStage(
            @PathVariable Long stageId,
            @AuthenticationPrincipal Jwt principal) throws AccessDeniedException {

        String username = principal.getSubject();
        Optional<User> coachUser = userRepository.findByUsername(username);

        quitPlanService.resetQuitPlanStageProgress(stageId, coachUser.get().getId());

        ResponseObject<Void> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Đã reset lại tiến độ giai đoạn cho người dùng.");
        response.setData(null);
        return ResponseEntity.ok(response);
    }



}

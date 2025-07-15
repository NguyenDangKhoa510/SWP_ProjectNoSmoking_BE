package org.datcheems.swp_projectnosmoking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.SmokingLogRequest;
import org.datcheems.swp_projectnosmoking.dto.response.SmokingLogResponse;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.service.SmokingLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/smoking-logs")
@RequiredArgsConstructor
public class SmokingLogController {

    private final SmokingLogService smokingLogService;
    private final UserRepository userRepository;


    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<SmokingLogResponse> createSmokingLog(@Valid @RequestBody SmokingLogRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String username = authentication.getName();


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Long userId = user.getId();


        SmokingLogResponse response = smokingLogService.createSmokingLog(request, userId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<List<SmokingLogResponse>> getMemberSmokingLogs() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Lấy user từ username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        List<SmokingLogResponse> logs = smokingLogService.getMemberSmokingLogs(userId);
        return ResponseEntity.ok(logs);
    }


    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/today")
    public ResponseEntity<SmokingLogResponse> getTodaySmokingLog() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        SmokingLogResponse log = smokingLogService.getTodaySmokingLog(userId);
        if (log == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(log);
    }




    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<SmokingLogResponse>> getLogsForMember(@PathVariable Long memberId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String coachUsername = authentication.getName();

        // Tìm user coach
        User coachUser = userRepository.findByUsername(coachUsername)
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        List<SmokingLogResponse> logs = smokingLogService.getSmokingLogsForCoach(memberId, coachUser.getId());

        return ResponseEntity.ok(logs);
    }





    @PostMapping("/check-missing")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkMissingLogs() {
        smokingLogService.checkMissingLogs();
        return ResponseEntity.ok("Missing logs check completed successfully");
    }
}
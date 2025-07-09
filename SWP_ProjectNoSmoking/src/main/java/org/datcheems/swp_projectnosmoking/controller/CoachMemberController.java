package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.MemberProfileResponse;
import org.datcheems.swp_projectnosmoking.service.CoachMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coach-members")
@RequiredArgsConstructor
public class CoachMemberController {

    private final CoachMemberService coachMemberService;

    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/my-members")
    public ResponseEntity<?> getMyMembers() {
        List<MemberProfileResponse> members = coachMemberService.getMyMembers();
        return ResponseEntity.ok(Map.of(
                "message", "Retrieved members successfully",
                "data", members
        ));
    }
}

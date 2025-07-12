package org.datcheems.swp_projectnosmoking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MemberInitialInfoRequest;
import org.datcheems.swp_projectnosmoking.service.MemberInitialInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member-initial-info")
@RequiredArgsConstructor
public class MemberInitialInfoController {

    private final MemberInitialInfoService service;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<?> submitInitialInfo(@Valid @RequestBody MemberInitialInfoRequest request) {
        var response = service.createOrUpdateInitialInfo(request);
        return ResponseEntity.ok(Map.of("message", "Initial info submitted successfully", "data", response));
    }

    @PreAuthorize("hasAnyRole('COACH', 'ADMIN')")
    @GetMapping("/my-members")
    public ResponseEntity<?> getMembersInitialInfo() {
        var list = service.getInitialInfosOfMyMembers();
        return ResponseEntity.ok(Map.of("message", "Initial infos retrieved", "data", list));
    }

    @GetMapping("/has-submitted")
    public ResponseEntity<Boolean> hasSubmitted() {
        boolean result = service.hasCurrentMemberSubmittedInitialInfo();
        return ResponseEntity.ok(result);
    }
}

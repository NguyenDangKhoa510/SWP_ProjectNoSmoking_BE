package org.datcheems.swp_projectnosmoking.controller;

import jakarta.validation.Valid;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.service.MemberService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/getAll")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/profile")
    public UserProfileResponse getProfile(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        return memberService.getCurrentUserProfile(username);
    }

    @PutMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal Jwt principal,
                                @Valid @RequestBody UserProfileUpdateRequest request) {
        String username = principal.getSubject();
        memberService.updateCurrentUserProfile(username, request);
        return "Profile updated successfully";
    }

    @PostMapping("/api/members/{memberId}/select-coach/{coachId}")
    public ResponseEntity<ResponseObject<String>> selectCoach(
            @PathVariable Long memberId,
            @PathVariable Long coachId
    ) {
        return memberService.selectCoach(memberId, coachId);
    }


}

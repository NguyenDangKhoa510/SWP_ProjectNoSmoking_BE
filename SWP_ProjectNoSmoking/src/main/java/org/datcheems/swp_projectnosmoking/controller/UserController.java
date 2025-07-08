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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/members/select-coach/{coachId}")
    public ResponseEntity<ResponseObject<String>> selectCoach(@PathVariable Long coachId) {
        return memberService.selectCoach(coachId);
    }
    @GetMapping("/getMyInfo")
    public UserProfileResponse getMyInfo(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        return memberService.getCurrentUserProfile(username);
    }    @GetMapping("/current-user-id")
    public ResponseEntity<Map<String, Object>> getCurrentUserId(@AuthenticationPrincipal Jwt principal) {
        try {
            String username = principal.getSubject();
            UserProfileResponse profile = memberService.getCurrentUserProfile(username);

            Map<String, Object> result = new HashMap<>();
            result.put("id", profile.getUserId());
            result.put("userId", profile.getUserId()); // Để tương thích với Frontend
            result.put("username", username);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Cannot get user ID");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Debug endpoint để test
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("UserController is working!");
    }
}

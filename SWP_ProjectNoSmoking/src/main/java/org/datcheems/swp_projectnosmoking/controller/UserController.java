package org.datcheems.swp_projectnosmoking.controller;

import jakarta.validation.Valid;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/profile")
    public UserProfileResponse getProfile(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        return userService.getCurrentUserProfile(username);
    }

    @PutMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal Jwt principal,
                                @Valid @RequestBody UserProfileUpdateRequest request) {
        String username = principal.getSubject();
        userService.updateCurrentUserProfile(username, request);
        return "Profile updated successfully";
    }


}

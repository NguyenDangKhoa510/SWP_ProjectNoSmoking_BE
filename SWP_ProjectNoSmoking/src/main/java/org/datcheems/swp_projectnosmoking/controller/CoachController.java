package org.datcheems.swp_projectnosmoking.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.datcheems.swp_projectnosmoking.dto.request.CoachProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.request.CoachRequest;
import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.response.CoachProfileResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.service.CoachService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CoachController {
    CoachService coachService;
    @PostMapping("/create")
    public ResponseEntity<ResponseObject<UserResponse>> createCoach(@RequestBody CoachRequest request) {
        return coachService.createCoach(request);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseObject<CoachProfileResponse>> getCurrentCoachProfile(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        return coachService.getCurrentCoachProfile(username);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject<CoachProfileResponse>> updateCurrentCoachProfile(@AuthenticationPrincipal Jwt principal, @RequestBody CoachProfileUpdateRequest request) {
        String username = principal.getSubject();
        return coachService.updateCurrentCoachProfile(username, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<CoachProfileResponse>> getCoachProfileById(@PathVariable Long id) {
        return coachService.getCoachProfileById(id);
    }

    @GetMapping("/getAllCoachProfiles")
    public ResponseEntity<ResponseObject<List<CoachProfileResponse>>> getAllCoachProfiles() {
        return coachService.getAllCoachProfile();
    }


}

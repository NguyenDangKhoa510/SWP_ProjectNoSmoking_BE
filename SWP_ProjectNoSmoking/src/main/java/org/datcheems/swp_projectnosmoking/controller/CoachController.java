package org.datcheems.swp_projectnosmoking.controller;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.datcheems.swp_projectnosmoking.dto.request.CoachRequest;
import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.service.CoachService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

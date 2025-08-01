package org.datcheems.swp_projectnosmoking.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.datcheems.swp_projectnosmoking.dto.request.AuthenticationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.RefreshTokenRequest;
import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.service.AuthenticationService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.datcheems.swp_projectnosmoking.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class AuthenticationController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<ResponseObject<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return result;
    }

    @PostMapping("/register")
    ResponseEntity<ResponseObject<UserResponse>> createUser(@RequestBody RegisterRequest request) {
        return userService.createUser(request);
    }

    @PostMapping("/refresh-token")
    ResponseEntity<ResponseObject<AuthenticationResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        return authenticationService.refreshToken(request.getRefreshToken());
    }

}

package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.request.AuthenticationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.service.AuthenticationService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return result;
    }

    @PostMapping("/register")
    UserResponse createUser(@RequestBody RegisterRequest request) {
        return userService.createUser(request);
    }


}

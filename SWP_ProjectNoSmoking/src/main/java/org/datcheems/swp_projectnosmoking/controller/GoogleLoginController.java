package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.GoogleLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleLoginController {
    @Autowired
    private GoogleLoginService googleLoginService;

    @PostMapping("/api/auth/google-login")
    public ResponseObject<AuthenticationResponse> googleLogin(@RequestBody String googleAccessToken) {
        return googleLoginService.authenticateWithGoogle(googleAccessToken);
    }
}

package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.GoogleLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GoogleLoginController {
    @Autowired
    private GoogleLoginService googleLoginService;

    @PostMapping("/api/auth/google-login")
    public ResponseObject<?> googleLogin(@RequestBody Map<String, String> body) {
        String accessToken = body.get("access_token");
        return googleLoginService.authenticateWithGoogle(accessToken);
    }

    @PostMapping("/api/auth/set-username")
    public ResponseEntity<?> setUsername(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String username = payload.get("username");

        ResponseObject<AuthenticationResponse> response = googleLoginService.createUserWithGoogle(email, username);
        return ResponseEntity.ok(response);
    }

}

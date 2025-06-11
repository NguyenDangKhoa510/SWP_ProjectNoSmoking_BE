package org.datcheems.swp_projectnosmoking.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.request.AuthenticationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.uitls.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
public class AuthenticationService {
    @Value("${jwt.signerKey}")
    private String SIGNING_KEY;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<ResponseObject<AuthenticationResponse>> authenticate(AuthenticationRequest request) {
        ResponseObject<AuthenticationResponse> response = new ResponseObject<>();

        try {
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!authenticated) {
                throw new RuntimeException("Invalid username or password");
            }

            var token = jwtUtils.generateToken(user);

            response.setStatus("success");
            response.setMessage("Authentication successful");
            response.setData(AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .build());

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (RuntimeException e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            response.setData(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}

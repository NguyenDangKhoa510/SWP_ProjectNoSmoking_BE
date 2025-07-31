package org.datcheems.swp_projectnosmoking.service;

import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.request.AuthenticationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.AuthenticationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public ResponseEntity<ResponseObject<AuthenticationResponse>> authenticate(AuthenticationRequest request) {
        ResponseObject<AuthenticationResponse> response = new ResponseObject<>();

        try {
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Sai tên đăng nhập hoặc mật khẩu"));

            if (user.getStatus() == User.Status.INACTIVE) {
                throw new RuntimeException("Tài khoản của bạn đang hạn chế");
            }

            if (user.getStatus() == User.Status.BANNED) {
                throw new RuntimeException("Tài khoản của bạn đã bị cấm");
            }

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!authenticated) {
                throw new RuntimeException("Sai tên đăng nhập hoặc mật khẩu");
            }

            var token = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(user);

            response.setStatus("success");
            response.setMessage("Authentication successful");
            response.setData(AuthenticationResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
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

    public ResponseEntity<ResponseObject<AuthenticationResponse>> refreshToken(String refreshToken) {
        ResponseObject<AuthenticationResponse> response = new ResponseObject<>();

        try {
            // Validate refresh token
            if (!jwtUtils.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid or expired refresh token");
            }

            // Extract username from refresh token
            String username = jwtUtils.extractUsername(refreshToken);

            // Find user by username
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate new access token
            var newToken = jwtUtils.generateToken(user);

            response.setStatus("success");
            response.setMessage("Token refreshed successfully");
            response.setData(AuthenticationResponse.builder()
                    .token(newToken)
                    .refreshToken(refreshToken) // Return the same refresh token
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

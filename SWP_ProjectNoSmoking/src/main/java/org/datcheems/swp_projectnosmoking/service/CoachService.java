package org.datcheems.swp_projectnosmoking.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.datcheems.swp_projectnosmoking.dto.request.CoachRequest;
import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.CoachRepository;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CoachService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    CoachRepository coachRepository;
    EmailService emailService;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject<UserResponse>> createCoach(CoachRequest request) {
        ResponseObject<UserResponse> response = new ResponseObject<>();

        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                response.setStatus("error");
                response.setMessage("Username already exists");
                response.setData(null);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            // ✅ Tạo mật khẩu random
            String rawPassword = RandomStringUtils.randomAlphanumeric(10);

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(rawPassword));

            Role coachRole = roleRepository.findByName(Role.RoleName.COACH)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.getRoles().clear();
            user.getRoles().add(coachRole);
            user.setStatus(User.Status.ACTIVE);

            User savedUser = userRepository.save(user);

            // Tạo CoachProfile
            Coach coachProfile = new Coach();
            coachProfile.setUser(savedUser);

            coachRepository.save(coachProfile);

            // ✅ Gửi email cho coach
            emailService.sendCoachCredentials(savedUser.getEmail(), savedUser.getUsername(), rawPassword);

            UserResponse userResponse = UserResponse.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .fullName(savedUser.getFullName())
                    .roles(savedUser.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet()))
                    .build();

            response.setStatus("success");
            response.setMessage("Coach account created successfully");
            response.setData(userResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.setStatus("error");
            response.setMessage("Failed to create coach: " + e.getMessage());
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

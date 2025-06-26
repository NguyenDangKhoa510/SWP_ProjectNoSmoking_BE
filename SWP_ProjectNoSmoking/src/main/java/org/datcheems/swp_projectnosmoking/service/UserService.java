package org.datcheems.swp_projectnosmoking.service;

import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.exception.ResourceNotFoundException;
import org.datcheems.swp_projectnosmoking.dto.request.RegisterRequest;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.mapper.UserMapper;
import org.datcheems.swp_projectnosmoking.repository.RoleRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public ResponseEntity<ResponseObject<UserResponse>> createUser(RegisterRequest request) {
        ResponseObject<UserResponse> response = new ResponseObject<>();

        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                response.setStatus("error");
                response.setMessage("Username already exists");
                response.setData(null);

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFullName(request.getFullName());

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            Role defaultRole = roleRepository.findByName(Role.RoleName.MEMBER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.getRoles().clear();
            user.getRoles().add(defaultRole);
            user.setStatus(User.Status.ACTIVE);

            User savedUser = userRepository.save(user);

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
            response.setMessage("User created successfully");
            response.setData(userResponse);

            if (savedUser.getRoles().stream().anyMatch(r -> r.getName().equals(Role.RoleName.MEMBER))) {
                Member member = new Member();
                member.setUser(savedUser);
                memberRepository.save(member);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.setStatus("error");
            response.setMessage("Failed to create user: " + e.getMessage());
            response.setData(null);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }



    public UserProfileResponse getCurrentUserProfile(String username) {
        // Tìm user theo username
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();

        // Tạo response object
        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setBirthDate(user.getBirthDate());
        response.setAddress(user.getAddress());

        // Trả response về
        return response;
    }



    public void updateCurrentUserProfile(String username, UserProfileUpdateRequest request) {
        // Tìm user theo username
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();

        // Cập nhật thông tin user từ request

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());
        user.setAddress(request.getAddress());

        // Lưu lại vào database
        userRepository.save(user);
    }











}

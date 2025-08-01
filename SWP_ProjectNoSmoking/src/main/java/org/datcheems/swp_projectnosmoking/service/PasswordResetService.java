package org.datcheems.swp_projectnosmoking.service;

import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.response.PasswordResetResponse;
import org.datcheems.swp_projectnosmoking.dto.request.PasswordResetRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.PasswordResetToken;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.PasswordResetTokenRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Value("${server.port}")
    private String serverPort;

    private static final int CODE_EXPIRATION_MINUTES = 30;
    private static final int CODE_LENGTH = 6;

    @Transactional
    public ResponseEntity<ResponseObject<String>> requestPasswordReset(PasswordResetRequest requestDto) {
        ResponseObject<String> response = new ResponseObject<>();

        try {
            Optional<User> userOptional = userRepository.findByEmail(requestDto.getEmail());
            if (userOptional.isEmpty()) {
                response.setStatus("error");
                response.setMessage("Không tìm thấy Tài khoản nào khớp với địa chỉ email này");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOptional.get();

            // Generate new verification code
            String code = generateVerificationCode(CODE_LENGTH);

            // Update existing token or create new one
            PasswordResetToken resetToken = tokenRepository.findByUser(user).orElse(new PasswordResetToken());
            resetToken.setUser(user);
            resetToken.setCode(code);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));

            tokenRepository.save(resetToken);

            // Send email with verification code
            String baseUrl = "http://localhost:" + serverPort;
            emailService.sendPasswordResetEmail(user.getEmail(), code, baseUrl);

            response.setStatus("success");
            response.setMessage("Password reset email sent successfully");
            response.setData("A verification code has been sent to your email address");

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.error("Error requesting password reset", e);
            // rollback bắt buộc nếu muốn giữ lại catch
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            response.setStatus("error");
            response.setMessage("Failed to process password reset request: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @Transactional
    public ResponseEntity<ResponseObject<String>> resetPassword(PasswordResetResponse resetDto) {
        ResponseObject<String> response = new ResponseObject<>();

        try {
            Optional<PasswordResetToken> tokenOptional = tokenRepository.findByCode(resetDto.getCode());
            if (tokenOptional.isEmpty()) {
                response.setStatus("error");
                response.setMessage("Invalid or expired verification code");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            PasswordResetToken resetToken = tokenOptional.get();
            if (resetToken.isExpired()) {
                tokenRepository.delete(resetToken);
                response.setStatus("error");
                response.setMessage("Verification code has expired");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            User user = resetToken.getUser();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
            userRepository.save(user);

            // Delete the used code
            tokenRepository.delete(resetToken);

            response.setStatus("success");
            response.setMessage("Password has been reset successfully");
            response.setData("You can now login with your new password");

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.error("Error resetting password", e);
            response.setStatus("error");
            response.setMessage("Failed to reset password: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<ResponseObject<Boolean>> validateCode(String code) {
        ResponseObject<Boolean> response = new ResponseObject<>();

        try {
            Optional<PasswordResetToken> tokenOptional = tokenRepository.findByCode(code);
            if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
                response.setStatus("error");
                response.setMessage("Invalid or expired verification code");
                response.setData(false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.setStatus("success");
            response.setMessage("Verification code is valid");
            response.setData(true);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.error("Error validating verification code", e);
            response.setStatus("error");
            response.setMessage("Failed to validate verification code: " + e.getMessage());
            response.setData(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String generateVerificationCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return code.toString();
    }
}

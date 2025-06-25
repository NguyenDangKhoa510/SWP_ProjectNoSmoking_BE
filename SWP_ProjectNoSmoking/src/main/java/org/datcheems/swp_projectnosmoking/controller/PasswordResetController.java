package org.datcheems.swp_projectnosmoking.controller;

<<<<<<< Updated upstream
import org.datcheems.swp_projectnosmoking.dto.response.PasswordResetResponse;
import org.datcheems.swp_projectnosmoking.dto.request.PasswordResetRequest;
=======
import org.datcheems.swp_projectnosmoking.dto.request.PasswordResetDto;
import org.datcheems.swp_projectnosmoking.dto.request.PasswordResetRequestDto;
>>>>>>> Stashed changes
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot")
<<<<<<< Updated upstream
    public ResponseEntity<ResponseObject<String>> requestPasswordReset(@RequestBody PasswordResetRequest requestDto) {
=======
    public ResponseEntity<ResponseObject<String>> requestPasswordReset(@RequestBody PasswordResetRequestDto requestDto) {
>>>>>>> Stashed changes
        return passwordResetService.requestPasswordReset(requestDto);
    }

    @PostMapping("/reset")
<<<<<<< Updated upstream
    public ResponseEntity<ResponseObject<String>> resetPassword(@RequestBody PasswordResetResponse resetDto) {
=======
    public ResponseEntity<ResponseObject<String>> resetPassword(@RequestBody PasswordResetDto resetDto) {
>>>>>>> Stashed changes
        return passwordResetService.resetPassword(resetDto);
    }

    @GetMapping("/validate-code")
    public ResponseEntity<ResponseObject<Boolean>> validateCode(@RequestParam String code) {
        return passwordResetService.validateCode(code);
    }
}

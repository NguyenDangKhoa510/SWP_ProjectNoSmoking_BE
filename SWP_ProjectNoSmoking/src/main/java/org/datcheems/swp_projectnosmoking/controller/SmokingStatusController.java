package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.request.SmokingStatusRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.SmokingStatusResponse;
import org.datcheems.swp_projectnosmoking.service.SmokingStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smoking-status")
@CrossOrigin(origins = {"http://localhost:5175", "http://localhost:3000"}) // Cho phép test từ Postman, React
public class SmokingStatusController {

    private final SmokingStatusService smokingStatusService;

    public SmokingStatusController(SmokingStatusService smokingStatusService) {
        this.smokingStatusService = smokingStatusService;
    }

    // Member xem tình trạng của chính mình
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/my")
    public SmokingStatusResponse getSmokingStatus(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaimAsString("sub"); // Lấy username từ Jwt token
        return smokingStatusService.getSmokingStatus(username);
    }

    // Member ghi nhận / update tình trạng của chính mình
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping
    public ResponseEntity<ResponseObject<SmokingStatusResponse>> saveOrUpdateSmokingStatus(@AuthenticationPrincipal Jwt principal,
                                                                                           @RequestBody SmokingStatusRequest request) {
        String username = principal.getClaimAsString("sub");

        // Save or update
        smokingStatusService.saveOrUpdateSmokingStatus(username, request);

        // Lấy lại data mới
        SmokingStatusResponse updatedStatus = smokingStatusService.getSmokingStatus(username);

        // Build ResponseObject
        ResponseObject<SmokingStatusResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Smoking status saved successfully");
        response.setData(updatedStatus);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Coach xem tình trạng của member bất kỳ
    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/{username}")
    public SmokingStatusResponse getSmokingStatusForUser(@PathVariable String username) {
        return smokingStatusService.getSmokingStatus(username);
    }
}

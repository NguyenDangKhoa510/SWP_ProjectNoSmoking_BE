package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.MemberCoachSelectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membercoachselection")
public class MemberCoachSelectionController {
    @Autowired
    private MemberCoachSelectionService selectionService;

    @GetMapping("/selection-id/me")
    public ResponseEntity<ResponseObject<Long>> getSelectionIdOfCurrentUser(@AuthenticationPrincipal Jwt principal) {
        ResponseObject<Long> response = new ResponseObject<>();

        try {
            String username = principal.getSubject();

            Long selectionId = selectionService.getSelectionIdByUsername(username);

            response.setStatus("success");
            response.setMessage(selectionId != null ? "Selection found" : "No selection found for user");
            response.setData(selectionId);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}

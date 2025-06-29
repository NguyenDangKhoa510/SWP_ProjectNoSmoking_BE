package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.UserMembershipRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserMembershipResponse;
import org.datcheems.swp_projectnosmoking.service.UserMembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-memberships")
@RequiredArgsConstructor
public class UserMembershipController {

    private final UserMembershipService userMembershipService;

    @GetMapping("/getAll")
    public ResponseEntity<ResponseObject<List<UserMembershipResponse>>> getAll() {
        return ResponseEntity.ok(userMembershipService.getAll());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseObject<UserMembershipResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userMembershipService.getById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject<UserMembershipResponse>> create(@RequestBody UserMembershipRequest request) {
        return ResponseEntity.ok(userMembershipService.create(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseObject<UserMembershipResponse>> update(@PathVariable Long id,
                                                                         @RequestBody UserMembershipRequest request) {
        return ResponseEntity.ok(userMembershipService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(userMembershipService.delete(id));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ResponseObject<Double>> getTotalRevenue() {
        return ResponseEntity.ok(userMembershipService.getTotalRevenue());
    }
}

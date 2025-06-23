package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MembershipPackageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MembershipPackageResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.MembershipPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-packages")
@RequiredArgsConstructor
public class MembershipPackageController {

    private final MembershipPackageService membershipPackageService;

    @GetMapping("/getAll")
    public ResponseEntity<ResponseObject<List<MembershipPackageResponse>>> getAllPackages() {
        return ResponseEntity.ok(membershipPackageService.getAllPackages());
    }

    @GetMapping("/getByID/{id}")
    public ResponseEntity<ResponseObject<MembershipPackageResponse>> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(membershipPackageService.getPackageById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject<MembershipPackageResponse>> createPackage(
            @RequestBody MembershipPackageRequest request) {
        return ResponseEntity.ok(membershipPackageService.createPackage(request));
    }

    @PutMapping("/updateByID/{id}")
    public ResponseEntity<ResponseObject<MembershipPackageResponse>> updatePackage(
            @PathVariable Long id,
            @RequestBody MembershipPackageRequest request) {
        return ResponseEntity.ok(membershipPackageService.updatePackage(id, request));
    }

    @DeleteMapping("/deleteByID/{id}")
    public ResponseEntity<ResponseObject<String>> deletePackage(@PathVariable Long id) {
        return ResponseEntity.ok(membershipPackageService.deletePackage(id));
    }
}

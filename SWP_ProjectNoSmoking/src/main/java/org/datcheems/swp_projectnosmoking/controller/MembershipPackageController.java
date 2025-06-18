package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MembershipPackageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MembershipPackageResponse;
import org.datcheems.swp_projectnosmoking.service.MembershipPackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-packages")
@RequiredArgsConstructor
public class MembershipPackageController {

    private final MembershipPackageService membershipPackageService;


    @GetMapping("/getallPackages")
    public ResponseEntity<List<MembershipPackageResponse>> getAllPackages() {
        return ResponseEntity.ok(membershipPackageService.getAllPackages());
    }


    @GetMapping("/getpackagebyid/{id}")
    public ResponseEntity<MembershipPackageResponse> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(membershipPackageService.getPackageById(id));
    }


    @PostMapping("/createpackage")
    public ResponseEntity<MembershipPackageResponse> createPackage(
            @RequestBody MembershipPackageRequest request) {
        return ResponseEntity.ok(membershipPackageService.createPackage(request));
    }


    @PutMapping("/updatepackage/{id}")
    public ResponseEntity<MembershipPackageResponse> updatePackage(
            @PathVariable Long id,
            @RequestBody MembershipPackageRequest request) {
        return ResponseEntity.ok(membershipPackageService.updatePackage(id, request));
    }

    
    @DeleteMapping("/deletepackage/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        membershipPackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

}

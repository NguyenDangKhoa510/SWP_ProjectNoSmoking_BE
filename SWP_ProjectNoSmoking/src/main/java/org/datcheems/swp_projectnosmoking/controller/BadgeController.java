package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.BadgeRequest;
import org.datcheems.swp_projectnosmoking.dto.response.BadgeResponse;
import org.datcheems.swp_projectnosmoking.service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping("/Create")
    public ResponseEntity<BadgeResponse> createBadge(@RequestBody BadgeRequest request) {
        return ResponseEntity.ok(badgeService.createBadge(request));
    }

    @GetMapping("/GetAll")
    public ResponseEntity<List<BadgeResponse>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @GetMapping("/GetById/{id}")
    public ResponseEntity<BadgeResponse> getBadgeById(@PathVariable int id) {
        return ResponseEntity.ok(badgeService.getBadgeById(id));
    }

    @PutMapping("/UpdateById/{id}")
    public ResponseEntity<BadgeResponse> updateBadge(@PathVariable int id, @RequestBody BadgeRequest request) {
        return ResponseEntity.ok(badgeService.updateBadge(id, request));
    }

    @DeleteMapping("/DeleteById/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable int id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.noContent().build();
    }
}

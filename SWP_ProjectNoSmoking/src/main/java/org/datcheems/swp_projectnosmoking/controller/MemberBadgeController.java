package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MemberBadgeRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MemberBadgeResponse;
import org.datcheems.swp_projectnosmoking.dto.response.MemberRankingResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.Badge;
import org.datcheems.swp_projectnosmoking.service.MemberBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-badge")
@RequiredArgsConstructor
public class MemberBadgeController {

    private final MemberBadgeService memberBadgeService;


    @PostMapping("/check-and-award/{memberId}")
    public ResponseEntity<ResponseObject<List<Badge>>> checkAndAwardBadges(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberBadgeService.checkAndAwardBadges(memberId));
    }


    @GetMapping("/getallbage/{memberId}")
    public ResponseEntity<ResponseObject<List<MemberBadgeResponse>>> getMemberBadges(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberBadgeService.getMemberBadges(memberId));
    }


    @GetMapping("/total-score/{memberId}")
    public ResponseEntity<ResponseObject<Integer>> getTotalScore(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberBadgeService.getTotalBadgeScore(memberId));
    }
//    @PostMapping("/admin/manualadd")
//    public ResponseEntity<ResponseObject<String>> assignBadgeManually(@RequestBody MemberBadgeRequest request) {
//        return ResponseEntity.ok(memberBadgeService.assignBadgeManually(request));
//    }
    @GetMapping("/ranking")
    public ResponseEntity<ResponseObject<List<MemberRankingResponse>>> getRanking() {
        return ResponseEntity.ok(memberBadgeService.getRankingByBadgeScore());
    }


}

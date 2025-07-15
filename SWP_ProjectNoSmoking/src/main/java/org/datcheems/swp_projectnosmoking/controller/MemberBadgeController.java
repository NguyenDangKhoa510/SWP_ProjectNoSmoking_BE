package org.datcheems.swp_projectnosmoking.controller;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MemberBadgeRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MemberBadgeResponse;
import org.datcheems.swp_projectnosmoking.dto.response.MemberRankingResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.MemberBadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-badge")
@RequiredArgsConstructor
public class MemberBadgeController {

    private final MemberBadgeService memberBadgeService;

    /**
     * Tự động cấp badge cho member (nếu đủ điều kiện)
     */
    @PostMapping("/check-and-award/{memberId}")
    public ResponseEntity<ResponseObject<String>> checkAndAwardBadges(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberBadgeService.checkAndAwardBadges(memberId));
    }

    /**
     * Lấy danh sách badge mà member đã nhận
     */
    @GetMapping("/getallbage/{memberId}")
    public ResponseEntity<ResponseObject<List<MemberBadgeResponse>>> getMemberBadges(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberBadgeService.getMemberBadges(memberId));
    }

    /**
     * Lấy tổng điểm badge của member
     */
    @GetMapping("/total-score/{memberId}")
    public ResponseEntity<ResponseObject<Integer>> getTotalScore(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberBadgeService.getTotalBadgeScore(memberId));
    }
    @PostMapping("/admin/manualadd")
    public ResponseEntity<ResponseObject<String>> assignBadgeManually(@RequestBody MemberBadgeRequest request) {
        return ResponseEntity.ok(memberBadgeService.assignBadgeManually(request));
    }
    @GetMapping("/ranking")
    public ResponseEntity<ResponseObject<List<MemberRankingResponse>>> getRanking() {
        return ResponseEntity.ok(memberBadgeService.getRankingByBadgeScore());
    }


}

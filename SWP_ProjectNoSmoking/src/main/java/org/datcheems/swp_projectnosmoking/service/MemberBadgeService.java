package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MemberBadgeRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MemberBadgeResponse;
import org.datcheems.swp_projectnosmoking.dto.response.MemberRankingResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberBadgeService {

    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final MemberRepository memberRepository;
    private final SmokingLogRepository smokingLogRepository;
    private final QuitPlanRepository quitPlanRepository;

    public ResponseObject<String> assignBadgeManually(MemberBadgeRequest request) {
        ResponseObject<String> response = new ResponseObject<>();

        Optional<Member> memberOpt = memberRepository.findById(request.getMemberId());
        Optional<Badge> badgeOpt = badgeRepository.findById(request.getBadgeId());

        if (memberOpt.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy member với ID = " + request.getMemberId());
            return response;
        }

        if (badgeOpt.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy badge với ID = " + request.getBadgeId());
            return response;
        }

        boolean alreadyAssigned = memberBadgeRepository.existsByMember_UserIdAndBadge_Id(
                request.getMemberId(), request.getBadgeId());

        if (alreadyAssigned) {
            response.setStatus("fail");
            response.setMessage("Huy hiệu này đã được gán cho member trước đó.");
            return response;
        }

        MemberBadge memberBadge = new MemberBadge();
        memberBadge.setMember(memberOpt.get());
        memberBadge.setBadge(badgeOpt.get());
        memberBadge.setAwardedDate(request.getAwardedDate() != null ? request.getAwardedDate() : LocalDate.now());
        memberBadgeRepository.save(memberBadge);

        response.setStatus("success");
        response.setMessage("Gán huy hiệu thành công cho member.");
        response.setData("Đã thêm badge ID " + request.getBadgeId() + " cho member ID " + request.getMemberId());
        return response;
    }

    public ResponseObject<List<Badge>> checkAndAwardBadges(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        ResponseObject<List<Badge>> response = new ResponseObject<>();

        if (optionalMember.isEmpty()) {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy thành viên với ID = " + memberId);
            response.setData(null);
            return response;
        }

        Member member = optionalMember.get();
        List<Badge> allBadges = badgeRepository.findAll();
        List<Badge> awardedBadges = new ArrayList<>();

        for (Badge badge : allBadges) {
            boolean alreadyAwarded = memberBadgeRepository.existsByMember_UserIdAndBadge_Id(memberId, badge.getId());

            if (!alreadyAwarded && meetsCondition(member, badge)) {
                MemberBadge memberBadge = new MemberBadge();
                memberBadge.setMember(member);
                memberBadge.setBadge(badge);
                memberBadge.setAwardedDate(LocalDate.now());
                memberBadgeRepository.save(memberBadge);
                awardedBadges.add(badge);
            }
        }

        response.setStatus("success");
        response.setMessage("Đã kiểm tra và cấp " + awardedBadges.size() + " badge mới (nếu có).");
        response.setData(awardedBadges);
        return response;
    }

    private boolean meetsCondition(Member member, Badge badge) {
        String type = badge.getType();
        int condition = badge.getCondition();

        // Non-smoking achievements
        if ("non-smoking".equals(type)) {
            return countNonSmokingDays(member) >= condition;
        }
        // Stage completion achievements
        else if ("stage-completion".equals(type)) {
            return countCompletedStages(member) >= condition;
        }

        return false;
    }

    private int countNonSmokingDays(Member member) {
        List<SmokingLog> logs = smokingLogRepository.findByMemberOrderByLogDateDesc(member);
        return (int) logs.stream()
                .filter(log -> log.getSmoked() != null && !log.getSmoked())
                .count();
    }

    private int countCompletedStages(Member member) {
        List<QuitPlan> quitPlans = quitPlanRepository.findByMember(member);
        return (int) quitPlans.stream()
                .flatMap(plan -> plan.getStages().stream())
                .filter(stage -> stage.getProgressPercentage() != null && stage.getProgressPercentage() >= 100.0)
                .count();
    }

    public ResponseObject<Integer> getTotalBadgeScore(Long memberId) {
        ResponseObject<Integer> response = new ResponseObject<>();
        if (!memberRepository.existsById(memberId)) {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy thành viên");
            response.setData(0);
            return response;
        }

        int totalScore = memberBadgeRepository.findByMember_UserId(memberId).stream()
                .mapToInt(b -> b.getBadge().getScore())
                .sum();

        response.setStatus("success");
        response.setMessage("Tính tổng điểm thành công");
        response.setData(totalScore);
        return response;
    }

    public ResponseObject<List<MemberBadgeResponse>> getMemberBadges(Long memberId) {
        ResponseObject<List<MemberBadgeResponse>> response = new ResponseObject<>();

        if (!memberRepository.existsById(memberId)) {
            response.setStatus("fail");
            response.setMessage("Không tìm thấy thành viên");
            response.setData(null);
            return response;
        }

        List<MemberBadgeResponse> badgeResponses = memberBadgeRepository.findByMember_UserId(memberId).stream()
                .map(memberBadge -> {
                    MemberBadgeResponse res = new MemberBadgeResponse();
                    res.setId(memberBadge.getId());
                    res.setBadgeName(memberBadge.getBadge().getName());
                    res.setBadgeDescription(memberBadge.getBadge().getDescription());
                    res.setIconUrl(memberBadge.getBadge().getIconUrl());
                    res.setScore(memberBadge.getBadge().getScore());
                    res.setAwardedDate(memberBadge.getAwardedDate());
                    return res;
                }).collect(Collectors.toList());

        response.setStatus("success");
        response.setMessage("Lấy danh sách badge thành công");
        response.setData(badgeResponses);
        return response;
    }
    public ResponseObject<List<MemberRankingResponse>> getRankingByBadgeScore() {
        List<Member> allMembers = memberRepository.findAll();

        List<MemberRankingResponse> rankings = allMembers.stream().map(member -> {
                    int totalScore = memberBadgeRepository.findByMember_UserId(member.getUserId())
                            .stream()
                            .mapToInt(mb -> mb.getBadge().getScore())
                            .sum();

                    MemberRankingResponse res = new MemberRankingResponse();
                    res.setMemberId(member.getUserId());
                    res.setFullName(member.getUser().getFullName());
                    res.setEmail(member.getUser().getEmail());
                    res.setAvatarUrl(member.getAvatarUrl());
                    res.setTotalScore(totalScore);
                    return res;
                }).sorted((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()))
                .collect(Collectors.toList());

        ResponseObject<List<MemberRankingResponse>> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Lấy bảng xếp hạng thành công");
        response.setData(rankings);
        return response;
    }

}

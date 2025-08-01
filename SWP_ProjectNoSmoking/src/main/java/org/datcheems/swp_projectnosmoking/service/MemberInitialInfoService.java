package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.MemberInitialInfoRequest;
import org.datcheems.swp_projectnosmoking.dto.response.MemberInitialInfoResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberInitialInfoService {

    private final MemberInitialInfoRepository repository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final CoachRepository coachRepository;
    private final MemberCoachSelectionRepository memberCoachSelectionRepository;


    public MemberInitialInfoResponse createOrUpdateInitialInfo(MemberInitialInfoRequest request) {
        User currentUser = getCurrentUser();
        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        MemberInitialInfo info = repository.findByMember(member).orElse(new MemberInitialInfo());
        info.setMember(member);
        info.setYearsSmoking(request.getYearsSmoking());
        info.setCigarettesPerDay(request.getCigarettesPerDay());
        info.setReasonToQuit(request.getReasonToQuit());
        info.setHealthStatus(request.getHealthStatus());

        repository.save(info);
        return toResponse(info);
    }


    public List<MemberInitialInfoResponse> getInitialInfosOfMyMembers() {
        User currentUser = getCurrentUser();

        Coach coach = coachRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        List<Member> members = memberCoachSelectionRepository.findByCoach(coach)
                .stream()
                .map(MemberCoachSelection::getMember)
                .collect(Collectors.toList());

        List<MemberInitialInfo> infos = repository.findByMemberIn(members);

        return infos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MemberInitialInfoResponse toResponse(MemberInitialInfo info) {
        MemberInitialInfoResponse res = new MemberInitialInfoResponse();
        res.setMemberId(info.getMember().getUserId());
        res.setFullName(info.getMember().getUser().getFullName());
        res.setYearsSmoking(info.getYearsSmoking());
        res.setCigarettesPerDay(info.getCigarettesPerDay());
        res.setReasonToQuit(info.getReasonToQuit());
        res.setHealthStatus(info.getHealthStatus());
        return res;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean hasCurrentMemberSubmittedInitialInfo() {
        User currentUser = getCurrentUser();
        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return repository.findByMember(member).isPresent();
    }

}

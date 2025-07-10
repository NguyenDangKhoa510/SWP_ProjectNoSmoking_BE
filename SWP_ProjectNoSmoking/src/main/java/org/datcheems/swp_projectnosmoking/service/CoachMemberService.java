package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.response.MemberProfileResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachMemberService {

    private final UserRepository userRepository;
    private final CoachRepository coachRepository;
    private final MemberCoachSelectionRepository memberCoachSelectionRepository;

    public List<MemberProfileResponse> getMyMembers() {
        User currentUser = getCurrentUser();

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        List<MemberCoachSelection> selections = memberCoachSelectionRepository.findByCoach(coach);

        return selections.stream()
                .map(selection -> {
                    Member member = selection.getMember();
                    User memberUser = member.getUser();

                    MemberProfileResponse dto = new MemberProfileResponse();
                    dto.setMemberId(member.getUserId());  // userId là memberId ở đây
                    dto.setUsername(memberUser.getUsername());
                    dto.setEmail(memberUser.getEmail());
                    dto.setFullName(memberUser.getFullName());

                    // Những dòng dưới phải lấy từ `member` chứ không phải `memberUser`
                    dto.setPhoneNumber(member.getPhoneNumber());
                    dto.setBirthDate(member.getBirthDate());
                    dto.setAddress(member.getAddress());
                    dto.setGender(member.getGender());
                    dto.setCreatedAt(memberUser.getCreatedAt());

                    return dto;
                })
                .collect(Collectors.toList());

    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

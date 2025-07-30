package org.datcheems.swp_projectnosmoking.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.exception.ResourceNotFoundException;
import org.datcheems.swp_projectnosmoking.repository.CoachRepository;
import org.datcheems.swp_projectnosmoking.repository.MemberCoachSelectionRepository;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MemberService {
    UserRepository userRepository;

    MemberRepository memberRepository;

    MemberCoachSelectionRepository memberCoachSelectionRepository;

    CoachRepository coachRepository;

    NotificationService notificationService;

    public UserProfileResponse getCurrentUserProfile(String username) {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();

        Optional<Member> optionalMember = memberRepository.findByUser(user);
        Member member = optionalMember.orElse(null);


        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());

        if (member != null) {
            response.setUserId(member.getUserId());
            response.setPhoneNumber(member.getPhoneNumber());
            response.setBirthDate(member.getBirthDate());
            response.setAddress(member.getAddress());
            response.setGender(member.getGender());
        } else {

            response.setPhoneNumber(null);
            response.setBirthDate(null);
            response.setAddress(null);
            response.setGender(null);

        }

        return response;
    }

    public void updateCurrentUserProfile(String username, UserProfileUpdateRequest request) {

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();


        user.setFullName(request.getFullName());
        userRepository.save(user);


        Member member = memberRepository.findByUser(user)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setUser(user);
                    return newMember;
                });

        member.setPhoneNumber(request.getPhoneNumber());
        member.setBirthDate(request.getBirthDate());
        member.setAddress(request.getAddress());
        member.setGender(request.getGender());

        memberRepository.save(member);
    }

    @Transactional
    public Map<String, Object> selectCoachForMember(Long coachId, String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User không tồn tại");
        }

        Optional<Coach> coachOpt = coachRepository.findById(coachId);
        if (coachOpt.isEmpty()) {
            throw new RuntimeException("Coach không tồn tại");
        }

        Optional<Member> memberOpt = memberRepository.findByUser(userOpt.get());
        if (memberOpt.isEmpty()) {
            throw new RuntimeException("Member không tồn tại");
        }

        Optional<MemberCoachSelection> selectionOpt = memberCoachSelectionRepository
                .findByMemberAndCoach(memberOpt.get(), coachOpt.get());

        MemberCoachSelection selection;
        if (selectionOpt.isEmpty()) {
            selection = new MemberCoachSelection();
            selection.setMember(memberOpt.get());
            selection.setCoach(coachOpt.get());
            selection.setSelectedAt(LocalDateTime.now());
            selection = memberCoachSelectionRepository.save(selection);

            // Gửi thông báo cho Coach
            notificationService.notifyCoachSelectedByMember(memberOpt.get().getUserId());
        } else {
            selection = selectionOpt.get();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("selectionId", selection.getSelectionId());
        result.put("memberId", memberOpt.get().getUserId());
        result.put("coachId", coachOpt.get().getUserId());

        return result;
    }


}

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
    public ResponseEntity<ResponseObject<String>> selectCoach(Long coachId) {
        ResponseObject<String> response = new ResponseObject<>();

        try {

            String username = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();


            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));


            Member member = memberRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));


            Coach coach = coachRepository.findById(coachId)
                    .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));

            boolean exists = memberCoachSelectionRepository
                    .existsByMemberAndCoach(member, coach);

            if (exists) {
                response.setStatus("success");
                response.setMessage("Member already selected this coach");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            MemberCoachSelection selection = new MemberCoachSelection();
            selection.setMember(member);
            selection.setCoach(coach);
            selection.setSelectedAt(LocalDateTime.now());

            memberCoachSelectionRepository.save(selection);

            response.setStatus("success");
            response.setMessage("Coach selected successfully");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (ResourceNotFoundException e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

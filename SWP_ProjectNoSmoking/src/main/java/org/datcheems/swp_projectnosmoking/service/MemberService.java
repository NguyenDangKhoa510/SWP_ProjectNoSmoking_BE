package org.datcheems.swp_projectnosmoking.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.exception.ResourceNotFoundException;
import org.datcheems.swp_projectnosmoking.repository.MemberCoachSelectionRepository;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public UserProfileResponse getCurrentUserProfile(String username) {
        // Tìm user theo username
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();

        // Tìm member tương ứng (dựa theo user_id)
        Optional<Member> optionalMember = memberRepository.findByUser(user);
        Member member = optionalMember.orElse(null);

        // Tạo response object
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
        // Tìm user theo username
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }

        User user = optionalUser.get();

        // Cập nhật thông tin User
        user.setFullName(request.getFullName());
        userRepository.save(user);

        // Tìm hoặc tạo Member liên kết
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
    public ResponseEntity<ResponseObject<String>> selectCoach(Long memberId, Long coachId) {
        ResponseObject<String> response = new ResponseObject<>();

        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

            User coach = userRepository.findById(coachId)
                    .orElseThrow(() -> new ResourceNotFoundException("Coach not found"));

            // Check coach role
            boolean isCoach = coach.getRoles().stream()
                    .anyMatch(role -> role.getName() == Role.RoleName.COACH);

            if (!isCoach) {
                response.setStatus("error");
                response.setMessage("Selected user is not a coach");
                response.setData(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

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

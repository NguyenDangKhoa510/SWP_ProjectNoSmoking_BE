package org.datcheems.swp_projectnosmoking.service;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.exception.ResourceNotFoundException;
import org.datcheems.swp_projectnosmoking.repository.MemberRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MemberService {
    UserRepository userRepository;

    MemberRepository memberRepository;

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

        // Cập nhật thông tin Member từ request
        member.setPhoneNumber(request.getPhoneNumber());
        member.setBirthDate(request.getBirthDate());
        member.setAddress(request.getAddress());
        member.setGender(request.getGender());


        memberRepository.save(member);
    }


}

package org.datcheems.swp_projectnosmoking.controller;

import jakarta.validation.Valid;
import org.datcheems.swp_projectnosmoking.dto.request.UserProfileUpdateRequest;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.dto.response.UserProfileResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserResponse;
import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.datcheems.swp_projectnosmoking.service.MemberService;
import org.datcheems.swp_projectnosmoking.service.NotificationService;
import org.datcheems.swp_projectnosmoking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private MemberCoachSelectionRepository memberCoachSelectionRepository;

    @Autowired
    private MessageRepository messageRepository;


    @GetMapping("/getAll")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/profile")
    public UserProfileResponse getProfile(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        return memberService.getCurrentUserProfile(username);
    }

    @PutMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal Jwt principal,
                                @Valid @RequestBody UserProfileUpdateRequest request) {
        String username = principal.getSubject();
        memberService.updateCurrentUserProfile(username, request);
        return "Profile updated successfully";
    }

    @GetMapping("/getMyInfo")
    public UserProfileResponse getMyInfo(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getSubject();
        return memberService.getCurrentUserProfile(username);
    }

//    @GetMapping("/current-user-id")
//    public ResponseEntity<Map<String, Object>> getCurrentUserId(@AuthenticationPrincipal Jwt principal) {
//        try {
//            String username = principal.getSubject();
//            UserProfileResponse profile = memberService.getCurrentUserProfile(username);
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("id", profile.getUserId());
//            result.put("userId", profile.getUserId()); // Để tương thích với Frontend
//            result.put("username", username);
//
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            Map<String, Object> error = new HashMap<>();
//            error.put("error", "Cannot get user ID");
//            error.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(error);
//        }
//    }

    @GetMapping("/members/selection-with-coach/{coachId}")
    public ResponseEntity<ResponseObject<Map<String, Object>>> getSelectionWithCoach(
            @PathVariable Long coachId,
            @AuthenticationPrincipal Jwt principal) {

        ResponseObject<Map<String, Object>> response = new ResponseObject<>();
        try {
            String username = principal.getSubject();

            Map<String, Object> result = memberService.selectCoachForMember(coachId, username);

            response.setStatus("success");
            response.setMessage("Lấy selection thành công");
            response.setData(result);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.setStatus("error");
            response.setMessage(e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/coaches/{coachId}/selections")
    public ResponseEntity<ResponseObject<List<Map<String, Object>>>> getCoachSelections(@PathVariable Long coachId) {
        ResponseObject<List<Map<String, Object>>> response = new ResponseObject<>();

        try {

            Optional<Coach> coachOpt = coachRepository.findById(coachId);
            if (coachOpt.isEmpty()) {
                response.setStatus("error");
                response.setMessage("Coach không tồn tại với ID: " + coachId);
                response.setData(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Coach coach = coachOpt.get();

            List<MemberCoachSelection> selections = memberCoachSelectionRepository.findByCoach(coach);

            List<Map<String, Object>> result = selections.stream().map(selection -> {
                Map<String, Object> selectionData = new HashMap<>();
                selectionData.put("selectionId", selection.getSelectionId());
                selectionData.put("selectedAt", selection.getSelectedAt());

                Long unreadCount = messageRepository.countUnreadMessages(selection.getSelectionId());
                selectionData.put("unreadCount", unreadCount);

                Member member = selection.getMember();
                Map<String, Object> memberData = new HashMap<>();
                memberData.put("memberId", member.getUserId());
                memberData.put("userId", member.getUser().getId()); // Sửa từ getUserId() thành getId()
                memberData.put("fullName", member.getUser().getFullName());
                memberData.put("username", member.getUser().getUsername());
                memberData.put("email", member.getUser().getEmail());

                selectionData.put("member", memberData);

                return selectionData;
            }).toList();

            response.setStatus("success");
            response.setMessage("Lấy danh sách selections thành công");
            response.setData(result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus("error");
            response.setMessage("Internal server error: " + e.getMessage());
            response.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/admin/users/{id}/status")
    public ResponseEntity<ResponseObject<UserResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam("status") User.Status status) {
        return userService.updateUserStatus(id, status);
    }

}

package org.datcheems.swp_projectnosmoking.controller;
import org.datcheems.swp_projectnosmoking.dto.request.BroadcastNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationBrief;
import org.datcheems.swp_projectnosmoking.utils.JwtUtils;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

import org.datcheems.swp_projectnosmoking.dto.request.NotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.UserNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserNotificationResponse;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
import org.datcheems.swp_projectnosmoking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.datcheems.swp_projectnosmoking.utils.JwtUtils.extractUserIdFromAuthentication;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<NotificationResponse> create(
            @RequestBody NotificationRequest dto,
            Authentication authentication) {

        Long createdById = extractUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(notificationService.createNotification(dto, createdById));
    }

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> send(@RequestBody UserNotificationRequest dto) {
        notificationService.sendNotificationToUser(dto);
        return ResponseEntity.ok("Notification sent successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER') or hasRole('COACH')")
    public ResponseEntity<List<UserNotificationResponse>> getMyNotifications(Authentication authentication) {
        Long userId = extractUserIdFromAuthentication(authentication);
        System.out.println("UserId in /me: " + userId);

        return ResponseEntity.ok(notificationService.getUserNotifications(userId));

    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationBrief>> getActiveNotifications() {
        return ResponseEntity.ok(notificationService.getActiveNotifications());
    }

    @PutMapping("/mark-as-read/{id}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('COACH')")
    public ResponseEntity<String> markAsRead(@PathVariable Long id, Authentication auth) {
        Long userId = extractUserIdFromAuthentication(auth);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok("Notification marked as read.");

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        List<NotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }


    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully");
    }


    @GetMapping("/sent-history")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<List<UserNotificationResponse>> getSentHistory(Authentication authentication) {
        Long coachUserId = JwtUtils.extractUserIdFromAuthentication(authentication);
        List<UserNotificationResponse> history = notificationService.getSentNotificationsByCoach(coachUserId);
        return ResponseEntity.ok(history);
    }




    @PostMapping("/send-to-member")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<String> sendNotificationToMyMember(
            @RequestBody UserNotificationRequest dto,
            Authentication authentication) {

        Long coachUserId = JwtUtils.extractUserIdFromAuthentication(authentication);

        notificationService.sendNotificationToMemberByCoach(dto, coachUserId);

        return ResponseEntity.ok("Notification sent successfully to member.");
    }



    @PostMapping("/send-to-members-and-coaches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendNotificationToMembersAndCoaches(@RequestBody BroadcastNotificationRequest dto) {
        notificationService.sendNotificationToAllMembersAndCoaches(dto);
        return ResponseEntity.ok("Notification sent successfully to all Members and Coaches.");
    }







    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<List<UserNotificationResponse>> getUserNotificationHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

}


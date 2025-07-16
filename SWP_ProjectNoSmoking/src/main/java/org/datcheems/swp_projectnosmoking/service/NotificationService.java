package org.datcheems.swp_projectnosmoking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.BroadcastNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.NotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.UserNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationBrief;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserNotificationResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.mapper.NotificationMapper;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;



import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final CoachRepository coachRepository;
    private final MemberCoachSelectionRepository memberCoachSelectionRepository;
    private final MemberRepository memberRepository;

    public NotificationResponse createNotification(NotificationRequest dto, Long createdById) {
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setTitle(dto.getTitle());
        notification.setContent(dto.getContent());
        notification.setIsActive(dto.getIsActive());
        notification.setCreatedBy(creator);

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDTO(saved);
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }




    public void sendNotificationToUser(UserNotificationRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = notificationRepository.findById(dto.getNotificationId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        userNotification.setPersonalizedReason(dto.getPersonalizedReason());
        userNotification.setDeliveryStatus(UserNotification.DeliveryStatus.SENT);

        userNotificationRepository.save(userNotification);
    }

    public List<UserNotificationResponse> getUserNotifications(Long userId) {
        List<UserNotification> list = userNotificationRepository.findByUserId(userId);
        return list.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }


    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        userNotificationRepository.deleteByNotificationId(notificationId);
        notificationRepository.delete(notification);
    }



    public void sendNotificationToMemberByCoach(UserNotificationRequest dto, Long coachUserId) {
        User coachUser = userRepository.findById(coachUserId)
                .orElseThrow(() -> new RuntimeException("Coach user not found"));

        Coach coach = coachRepository.findByUserId(coachUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));


        Member member = memberRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        boolean isLinked = memberCoachSelectionRepository.existsByMemberAndCoach(member, coach);
        if (!isLinked) {
            throw new RuntimeException("You can only send notifications to members who have selected you as their coach.");
        }


        sendNotificationToUser(dto);
    }



    public List<UserNotificationResponse> getSentNotificationsByCoach(Long coachUserId) {
        List<UserNotification> sentNotifications = userNotificationRepository.findByNotification_CreatedBy_Id(coachUserId);

        return sentNotifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public void sendNotificationToAllMembersAndCoaches(BroadcastNotificationRequest dto) {
        Notification notification = notificationRepository.findById(dto.getNotificationId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        List<User> targetUsers = userRepository.findByRoleNames(List.of("MEMBER", "COACH"));

        for (User user : targetUsers) {
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(user);
            userNotification.setNotification(notification);
            userNotification.setPersonalizedReason(dto.getPersonalizedReason() != null ? dto.getPersonalizedReason() : "System-wide announcement");
            userNotification.setDeliveryStatus(UserNotification.DeliveryStatus.SENT);

            userNotificationRepository.save(userNotification);
        }
    }





    public List<NotificationBrief> getActiveNotifications() {
        return notificationRepository.findByIsActiveTrue()
                .stream()
                .map(notificationMapper::toBriefDTO)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId, Long userId) {
        UserNotification un = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!un.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Not allowed");
        }
        un.setIsRead(true);
        userNotificationRepository.save(un);
    }



}


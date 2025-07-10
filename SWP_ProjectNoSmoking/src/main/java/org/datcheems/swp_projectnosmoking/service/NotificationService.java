package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.NotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.UserNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationBrief;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.UserNotificationResponse;
import org.datcheems.swp_projectnosmoking.entity.Notification;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.entity.UserNotification;
import org.datcheems.swp_projectnosmoking.mapper.NotificationMapper;
import org.datcheems.swp_projectnosmoking.repository.NotificationRepository;
import org.datcheems.swp_projectnosmoking.repository.UserNotificationRepository;
import org.datcheems.swp_projectnosmoking.repository.UserRepository;
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


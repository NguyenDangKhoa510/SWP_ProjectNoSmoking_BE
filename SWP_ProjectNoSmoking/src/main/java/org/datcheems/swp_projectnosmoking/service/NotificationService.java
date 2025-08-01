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

    public void notifyCoachStageCompleted(Long memberId, int stageNumber) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        User memberUser = member.getUser();

        MemberCoachSelection selection = memberCoachSelectionRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("No coach found for this member"));

        Coach coach = selection.getCoach();
        User coachUser = coach.getUser();

        Notification notification = new Notification();
        notification.setTitle("Thành viên đã hoàn thành giai đoạn");
        notification.setContent("Thành viên " + memberUser.getFullName() + " đã hoàn thành giai đoạn số " + stageNumber + ".");
        notification.setIsActive(true);
        notification.setCreatedBy(memberUser);

        Notification savedNotification = notificationRepository.save(notification);

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(coachUser);
        userNotification.setNotification(savedNotification);
        userNotification.setPersonalizedReason("Thông báo từ hệ thống về tiến độ học viên.");
        userNotification.setDeliveryStatus(UserNotification.DeliveryStatus.SENT);

        userNotificationRepository.save(userNotification);
    }

    public void notifyMemberStageUpdatedByCoach(Long coachUserId, Long memberId, int stageNumber) {
        User coachUser = userRepository.findById(coachUserId)
                .orElseThrow(() -> new RuntimeException("Coach user not found"));

        Coach coach = coachRepository.findByUserId(coachUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        User memberUser = member.getUser();

        boolean isLinked = memberCoachSelectionRepository.existsByMemberAndCoach(member, coach);
        if (!isLinked) {
            throw new RuntimeException("This coach is not assigned to the member.");
        }

        Notification notification = new Notification();
        notification.setTitle("Huấn luyện viên đã cập nhật tiến độ");
        notification.setContent("Huấn luyện viên " + coachUser.getFullName() +
                " đã cập nhật bạn đến giai đoạn số " + stageNumber + ".");
        notification.setIsActive(true);
        notification.setCreatedBy(coachUser);

        Notification savedNotification = notificationRepository.save(notification);

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(memberUser);
        userNotification.setNotification(savedNotification);
        userNotification.setPersonalizedReason("Thông báo từ huấn luyện viên về tiến độ.");
        userNotification.setDeliveryStatus(UserNotification.DeliveryStatus.SENT);

        userNotificationRepository.save(userNotification);
    }

    public void notifyCoachStageFailed(Long memberUserId, int stageNumber) {
        // Lấy coach từ mối quan hệ đã chọn
        Member member = memberRepository.findById(memberUserId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        User memberUser = member.getUser();

        MemberCoachSelection selection = memberCoachSelectionRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy huấn luyện viên của thành viên"));

        User coachUser = selection.getCoach().getUser();

        // Tạo thông báo
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTitle("Thành viên chưa hoàn thành giai đoạn");
        notificationRequest.setContent("Thành viên " + memberUser.getFullName() + " đã không hoàn thành giai đoạn "
                + stageNumber + " trong kế hoạch cai thuốc.");
        notificationRequest.setIsActive(true);

        // Lấy admin để gán người tạo
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Tài khoản quản trị viên không tồn tại")));

        // Lưu notification
        NotificationResponse notificationResponse = createNotification(notificationRequest, admin.getId());

        // Gửi đến coach
        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(coachUser.getId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason("Thành viên " + memberUser.getFullName()
                + " không hoàn thành giai đoạn " + stageNumber);

        sendNotificationToUser(userNotificationRequest);
    }

    public void notifyCoachSelectedByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));
        User memberUser = member.getUser();

        MemberCoachSelection selection = memberCoachSelectionRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("Thành viên chưa chọn huấn luyện viên"));

        User coachUser = selection.getCoach().getUser();

        // Dùng admin làm người tạo
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Tài khoản admin không tồn tại")));

        // Tạo notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTitle("Bạn đã được chọn làm huấn luyện viên");
        notificationRequest.setContent("Thành viên " + memberUser.getFullName()
                + " đã chọn bạn làm người đồng hành trong quá trình cai thuốc. Hãy tạo kế hoạch cho họ.");
        notificationRequest.setIsActive(true);

        // Lưu notification (dùng admin là người tạo)
        NotificationResponse notificationResponse = createNotification(notificationRequest, admin.getId());

        // Gửi tới coach
        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(coachUser.getId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason("Bạn được chọn làm huấn luyện viên cho " + memberUser.getFullName());

        sendNotificationToUser(userNotificationRequest);
    }

    public void notifyMemberStageResetByCoach(Long coachUserId, Long memberId, int stageNumber) {
        // Lấy thông tin Coach
        User coachUser = userRepository.findById(coachUserId)
                .orElseThrow(() -> new RuntimeException("Coach user not found"));

        Coach coach = coachRepository.findByUserId(coachUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        // Lấy thông tin Member
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        User memberUser = member.getUser();

        // Kiểm tra mối liên kết giữa Coach và Member
        boolean isLinked = memberCoachSelectionRepository.existsByMemberAndCoach(member, coach);
        if (!isLinked) {
            throw new RuntimeException("This coach is not assigned to the member.");
        }

        // Tạo Notification
        Notification notification = new Notification();
        notification.setTitle("Tiến độ giai đoạn đã được đặt lại");
        notification.setContent("Huấn luyện viên " + coachUser.getFullName() +
                " đã đặt lại tiến độ cho giai đoạn số " + stageNumber + " của bạn. Hãy bắt đầu lại từ đầu.");
        notification.setIsActive(true);
        notification.setCreatedBy(coachUser);

        Notification savedNotification = notificationRepository.save(notification);

        // Gán thông báo cho Member
        UserNotification userNotification = new UserNotification();
        userNotification.setUser(memberUser);
        userNotification.setNotification(savedNotification);
        userNotification.setPersonalizedReason("Thông báo từ huấn luyện viên về việc đặt lại giai đoạn.");
        userNotification.setDeliveryStatus(UserNotification.DeliveryStatus.SENT);
        userNotification.setIsRead(false);

        userNotificationRepository.save(userNotification);
    }

}


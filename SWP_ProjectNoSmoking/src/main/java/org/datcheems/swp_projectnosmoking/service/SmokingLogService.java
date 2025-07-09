package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.NotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.SmokingLogRequest;
import org.datcheems.swp_projectnosmoking.dto.request.UserNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.SmokingLogResponse;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.SmokingLog;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.datcheems.swp_projectnosmoking.mapper.SmokingLogMapper;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmokingLogService {

    private final SmokingLogRepository smokingLogRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SmokingLogMapper smokingLogMapper;
    private final MemberCoachSelectionRepository memberCoachSelectionRepository;
    private final MemberInitialInfoRepository memberInitialInfoRepository;


    @Transactional
    public SmokingLogResponse createSmokingLog(SmokingLogRequest request, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        validateMemberCanLog(member);

        // Check if log already exists for this date
        LocalDate logDate = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();
        Optional<SmokingLog> existingLog = smokingLogRepository.findByMemberAndLogDate(member, logDate);

        if (existingLog.isPresent()) {
            // Update existing log
            SmokingLog log = existingLog.get();
            Integer previousCount = log.getSmokeCount();
            smokingLogMapper.updateEntityFromRequest(request, log);
            SmokingLog savedLog = smokingLogRepository.save(log);

            // Send notification based on smoking habit change
            sendSmokingHabitChangeNotification(member, previousCount, savedLog.getSmokeCount());

            SmokingLogResponse response = smokingLogMapper.toResponse(savedLog);
            response.setPreviousSmokeCount(previousCount);
            response.setIsImprovement(savedLog.getSmokeCount() <= previousCount);
            return response;
        } else {
            // Create new log
            SmokingLog log = smokingLogMapper.toEntity(request, member);
            log.setLogDate(logDate);
            SmokingLog savedLog = smokingLogRepository.save(log);

            // Get previous log for comparison
            List<SmokingLog> previousLogs = smokingLogRepository.findPreviousLogs(member, logDate);
            Integer previousCount = previousLogs.isEmpty() ? null : previousLogs.get(0).getSmokeCount();

            // Send notification based on smoking habit change if previous log exists
            if (previousCount != null) {
                sendSmokingHabitChangeNotification(member, previousCount, savedLog.getSmokeCount());
            }

            SmokingLogResponse response = smokingLogMapper.toResponse(savedLog);
            response.setPreviousSmokeCount(previousCount);
            response.setIsImprovement(previousCount == null || savedLog.getSmokeCount() <= previousCount);
            return response;
        }
    }

    public List<SmokingLogResponse> getMemberSmokingLogs(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        validateMemberCanLog(member);

        List<SmokingLog> logs = smokingLogRepository.findByMemberOrderByLogDateDesc(member);

        return logs.stream()
                .map(log -> {
                    SmokingLogResponse response = smokingLogMapper.toResponse(log);

                    // Find previous log for comparison
                    List<SmokingLog> previousLogs = smokingLogRepository.findPreviousLogs(member, log.getLogDate());
                    Integer previousCount = previousLogs.isEmpty() ? null : previousLogs.get(0).getSmokeCount();

                    response.setPreviousSmokeCount(previousCount);
                    response.setIsImprovement(previousCount == null || log.getSmokeCount() <= previousCount);

                    return response;
                })
                .collect(Collectors.toList());
    }

    public SmokingLogResponse getTodaySmokingLog(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        validateMemberCanLog(member);

        LocalDate today = LocalDate.now();
        Optional<SmokingLog> todayLog = smokingLogRepository.findTodayLog(member, today);

        if (todayLog.isPresent()) {
            SmokingLog log = todayLog.get();
            SmokingLogResponse response = smokingLogMapper.toResponse(log);

            // Lấy log gần nhất trước hôm nay
            List<SmokingLog> previousLogs = smokingLogRepository.findPreviousLogs(member, today);
            Integer previousCount = previousLogs.isEmpty() ? null : previousLogs.get(0).getSmokeCount();

            response.setPreviousSmokeCount(previousCount);
            response.setIsImprovement(previousCount == null || log.getSmokeCount() <= previousCount);

            return response;
        } else {
            return null;
        }
    }


    private void validateMemberCanLog(Member member) {
        // Check đã chọn coach chưa
        boolean hasCoach = memberCoachSelectionRepository.existsByMember(member);
        if (!hasCoach) {
            throw new RuntimeException("You must select a coach before logging smoking data.");
        }

        // Check đã điền thông tin sơ bộ chưa
        boolean hasInitialInfo = memberInitialInfoRepository.findByMember(member).isPresent();
        if (!hasInitialInfo) {
            throw new RuntimeException("You must submit your initial information before logging smoking data.");
        }
    }



    @Transactional
    public void checkMissingLogs() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Member> membersWithoutLog = smokingLogRepository.findMembersWithoutLogForDate(yesterday);

        for (Member member : membersWithoutLog) {
            sendMissingLogNotification(member);
        }
    }

    private void sendMissingLogNotification(Member member) {
        // Create notification for missing log
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTitle("Missing Smoking Log");
        notificationRequest.setContent("You forgot to log your smoking data yesterday. Please remember to log your data daily to track your progress.");
        notificationRequest.setIsActive(true);

        // Find admin user to set as creator (using a default admin user ID or finding by username/email)
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Admin user not found")));

        // Create notification and get the response with the ID
        NotificationResponse notificationResponse = notificationService.createNotification(notificationRequest, admin.getId());

        // Send notification to user
        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(member.getUserId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason("Daily smoking log reminder");

        notificationService.sendNotificationToUser(userNotificationRequest);
    }

    private void sendSmokingHabitChangeNotification(Member member, Integer previousCount, Integer currentCount) {
        if (previousCount == null || previousCount.equals(currentCount)) {
            return;
        }

        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setIsActive(true);

        // Find admin user to set as creator (using a default admin user ID or finding by username/email)
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Admin user not found")));

        if (currentCount < previousCount) {
            // Encouragement notification
            notificationRequest.setTitle("Great Progress!");
            notificationRequest.setContent("Congratulations! You've reduced your smoking from " + previousCount + 
                    " to " + currentCount + " cigarettes. Keep up the good work!");
        } else {
            // Warning notification
            notificationRequest.setTitle("Smoking Increase Alert");
            notificationRequest.setContent("We noticed your smoking has increased from " + previousCount + 
                    " to " + currentCount + " cigarettes. Remember your goal to quit smoking. Stay strong!");
        }

        // Create notification and get the response with the ID
        NotificationResponse notificationResponse = notificationService.createNotification(notificationRequest, admin.getId());

        // Send notification to user
        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(member.getUserId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason(currentCount < previousCount ? 
                "Smoking reduction achievement" : "Smoking increase warning");

        notificationService.sendNotificationToUser(userNotificationRequest);
    }
}

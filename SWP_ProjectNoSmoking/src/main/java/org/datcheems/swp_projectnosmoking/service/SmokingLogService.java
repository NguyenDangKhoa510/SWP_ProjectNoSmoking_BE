package org.datcheems.swp_projectnosmoking.service;

import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.NotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.request.SmokingLogRequest;
import org.datcheems.swp_projectnosmoking.dto.request.UserNotificationRequest;
import org.datcheems.swp_projectnosmoking.dto.response.NotificationResponse;
import org.datcheems.swp_projectnosmoking.dto.response.SmokingLogResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
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
    private final QuitPlanRepository quitPlanRepository;
    private final QuitPlanStageRepository quitPlanStageRepository;


    @Transactional
    public SmokingLogResponse createSmokingLog(SmokingLogRequest request, Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));


        validateMemberCanLog(member);

        // Check if log already exists for this date
        LocalDate logDate = request.getLogDate() != null ? request.getLogDate() : LocalDate.now();
        Optional<SmokingLog> existingLog = smokingLogRepository.findByMemberAndLogDate(member, logDate);

        if (existingLog.isPresent()) {
            // Update existing log
            SmokingLog log = existingLog.get();
            Integer previousCount = log.getSmokeCount();
            smokingLogMapper.updateEntityFromRequest(request, log);

            QuitPlanStage matchedStage = findMatchedStageForDate(member, logDate);
            log.setQuitPlanStage(matchedStage);

            SmokingLog savedLog = smokingLogRepository.save(log);

            if (savedLog.getQuitPlanStage() != null) {
                QuitPlanStage stage = savedLog.getQuitPlanStage();

                double progress = calculateStageProgress(stage, member);
                stage.setProgressPercentage(progress);
                quitPlanStageRepository.save(stage);

                boolean completed = isStageCompleted(stage, member);
                if (completed && stage.getStatus() != QuitPlanStageStatus.completed) {
                    stage.setStatus(QuitPlanStageStatus.completed);
                    quitPlanStageRepository.save(stage);

                    // Gửi notification chúc mừng
                    sendStageCompletionNotification(member, stage);
                }
            }

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

            QuitPlanStage matchedStage = findMatchedStageForDate(member, logDate);
            log.setQuitPlanStage(matchedStage);

            SmokingLog savedLog = smokingLogRepository.save(log);

            if (savedLog.getQuitPlanStage() != null) {
                QuitPlanStage stage = savedLog.getQuitPlanStage();

                double progress = calculateStageProgress(stage, member);
                stage.setProgressPercentage(progress);
                quitPlanStageRepository.save(stage);

                boolean completed = isStageCompleted(stage, member);
                if (completed && stage.getStatus() != QuitPlanStageStatus.completed) {
                    stage.setStatus(QuitPlanStageStatus.completed);
                    quitPlanStageRepository.save(stage);

                    sendStageCompletionNotification(member, stage);
                }
            }

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

    private QuitPlanStage findMatchedStageForDate(Member member, LocalDate logDate) {
        List<QuitPlan> plans = quitPlanRepository.findByMember(member);

        if (plans.isEmpty()) {
            return null;
        }

        QuitPlan quitPlan = plans.stream()
                .filter(p -> p.getStatus() == QuitPlanStatus.active)
                .findFirst()
                .orElse(null);

        if (quitPlan == null) {
            return null;
        }

        List<QuitPlanStage> stages = quitPlan.getStages();

        return stages.stream()
                .filter(stage ->
                        stage.getStartDate() != null &&
                                stage.getEndDate() != null &&
                                !logDate.isBefore(stage.getStartDate()) &&
                                !logDate.isAfter(stage.getEndDate())
                )
                .findFirst()
                .orElse(null);
    }


    public List<SmokingLogResponse> getMemberSmokingLogs(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));


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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));


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
            throw new RuntimeException("Bạn phải chọn huấn luyện viên trước khi ghi nhật ký hút thuốc.");

        }

        // Check đã điền thông tin sơ bộ chưa
        boolean hasInitialInfo = memberInitialInfoRepository.findByMember(member).isPresent();
        if (!hasInitialInfo) {
            throw new RuntimeException("Bạn cần điền thông tin sơ bộ trước khi ghi nhật ký hút thuốc.");

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
        notificationRequest.setTitle("Quên ghi nhật ký hút thuốc");
        notificationRequest.setContent("Bạn đã quên ghi nhật ký hút thuốc ngày hôm qua. Hãy nhớ ghi nhật ký mỗi ngày để theo dõi tiến trình cai thuốc.");
        notificationRequest.setIsActive(true);

        // Find admin user to set as creator (using a default admin user ID or finding by username/email)
        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Tài khoản quản trị viên không tồn tại")));



        // Create notification and get the response with the ID
        NotificationResponse notificationResponse = notificationService.createNotification(notificationRequest, admin.getId());

        // Send notification to user
        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(member.getUserId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason("Bạn quên ghi nhật ký hút thuốc hôm qua. Vui lòng cập nhật để theo dõi tiến trình.");


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
                        .orElseThrow(() -> new RuntimeException("Tài khoản quản trị viên không tồn tại")));


        if (currentCount < previousCount) {
            // Encouragement notification
            notificationRequest.setTitle("Tiến bộ tuyệt vời!");
            notificationRequest.setContent("Congratulations! You've reduced your smoking from " + previousCount + 
                    " to " + currentCount + " cigarettes. Keep up the good work!");
        } else {
            // Warning notification
            notificationRequest.setTitle("Smoking Increase Alert");
            notificationRequest.setContent("Chúc mừng bạn! Bạn đã giảm số điếu thuốc từ " + previousCount +
                    " xuống còn " + currentCount + ". Tiếp tục phát huy nhé!");

        }

        // Create notification and get the response with the ID
        NotificationResponse notificationResponse = notificationService.createNotification(notificationRequest, admin.getId());

        // Send notification to user
        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(member.getUserId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason(currentCount < previousCount ?
                "Bạn đã giảm số lượng thuốc hút – cố gắng duy trì nhé!" :
                "Số lượng thuốc hút đã tăng – hãy kiên trì với mục tiêu cai thuốc!");


        notificationService.sendNotificationToUser(userNotificationRequest);
    }

    public boolean isStageCompleted(QuitPlanStage stage, Member member) {
        if (stage.getStartDate() == null || stage.getEndDate() == null || stage.getTargetCigaretteCount() == null) {
            return false;
        }

        LocalDate startDate = stage.getStartDate();
        LocalDate endDate = stage.getEndDate();
        Integer target = stage.getTargetCigaretteCount();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Optional<SmokingLog> logOpt = smokingLogRepository.findByMemberAndLogDate(member, date);

            if (logOpt.isEmpty()) {
                return false;
            }

            SmokingLog log = logOpt.get();

            if (log.getSmokeCount() != null && log.getSmokeCount() > target) {
                return false;
            }
        }

        return true;
    }

    private void sendStageCompletionNotification(Member member, QuitPlanStage stage) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTitle("Hoàn thành giai đoạn!");
        notificationRequest.setContent("Chúc mừng! Bạn đã hoàn thành giai đoạn "
                + stage.getStageNumber() + " trong kế hoạch cai thuốc. Tiếp tục cố gắng nhé!");
        notificationRequest.setIsActive(true);

        User admin = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Tài khoản quản trị viên không tồn tại")));


        NotificationResponse notificationResponse =
                notificationService.createNotification(notificationRequest, admin.getId());

        UserNotificationRequest userNotificationRequest = new UserNotificationRequest();
        userNotificationRequest.setUserId(member.getUserId());
        userNotificationRequest.setNotificationId(notificationResponse.getNotificationId());
        userNotificationRequest.setPersonalizedReason("Bạn đã hoàn thành một giai đoạn trong kế hoạch cai thuốc – tiếp tục cố gắng nhé!");

        notificationService.sendNotificationToUser(userNotificationRequest);
    }

    public double calculateStageProgress(QuitPlanStage stage, Member member) {
        if (stage.getStartDate() == null || stage.getEndDate() == null) {
            return 0.0;
        }

        LocalDate startDate = stage.getStartDate();
        LocalDate endDate = stage.getEndDate();

        long totalDays = startDate.datesUntil(endDate.plusDays(1)).count();
        long daysWithLog = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Optional<SmokingLog> logOpt = smokingLogRepository.findByMemberAndLogDate(member, date);
            if (logOpt.isPresent()) {
                SmokingLog log = logOpt.get();

                // Nếu muốn tính đúng target, kiểm tra smokeCount ≤ target
                if (stage.getTargetCigaretteCount() != null &&
                        log.getSmokeCount() != null &&
                        log.getSmokeCount() > stage.getTargetCigaretteCount()) {
                    continue; // Không tính ngày vượt target
                }

                daysWithLog++;
            }
        }

        if (totalDays == 0) return 0.0;

        double percent = (double) daysWithLog / totalDays * 100;
        return Math.round(percent * 10.0) / 10.0;
    }



}

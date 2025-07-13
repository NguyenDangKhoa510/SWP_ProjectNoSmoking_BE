package org.datcheems.swp_projectnosmoking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanRequest;
import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanStageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanResponse;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanStageResponse;
import org.datcheems.swp_projectnosmoking.entity.*;
import org.datcheems.swp_projectnosmoking.entity.Role;
import org.datcheems.swp_projectnosmoking.mapper.QuitPlanMapper;
import org.datcheems.swp_projectnosmoking.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuitPlanService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final CoachRepository coachRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final QuitPlanStageRepository quitPlanStageRepository;
    private final QuitPlanMapper quitPlanMapper;


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isCoach(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> Role.RoleName.COACH.equals(role.getName()));
    }


    private boolean isMember(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> Role.RoleName.MEMBER.equals(role.getName()));
    }


    @Transactional
    public QuitPlanResponse createQuitPlan(QuitPlanRequest request) {
        User currentUser = getCurrentUser();

        if (!isCoach(currentUser)) {
            throw new RuntimeException("Only Coaches can create Quit Plans");
        }

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        Member member = memberRepository.findByUserId(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member profile not found for userId: " + request.getMemberId()));

        QuitPlan plan = new QuitPlan();
        plan.setMember(member);
        plan.setCoach(coach);
        plan.setReasonToQuit(request.getReasonToQuit());
        plan.setTotalStages(3);
        plan.setGoal(request.getGoal());
        plan.setStatus(QuitPlanStatus.active);

        QuitPlan savedPlan = quitPlanRepository.save(plan);

        List<QuitPlanStage> stages = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            QuitPlanStage stage = new QuitPlanStage();
            stage.setQuitPlan(savedPlan);
            stage.setStageNumber(i);
            stage.setStatus(QuitPlanStageStatus.active);
            stages.add(stage);
        }

        quitPlanStageRepository.saveAll(stages);
        savedPlan.setStages(stages);

        return quitPlanMapper.toResponse(savedPlan);
    }

    public QuitPlanResponse getQuitPlanById(Long quitPlanId) {
        User currentUser = getCurrentUser();
        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new RuntimeException("Quit plan not found"));

        if (isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) {
            return quitPlanMapper.toResponse(quitPlan);
        } else if (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId())) {
            return quitPlanMapper.toResponse(quitPlan);
        } else {
            throw new RuntimeException("You don't have permission to view this quit plan");
        }
    }


    public List<QuitPlanResponse> getQuitPlansForCurrentMember() {
        User currentUser = getCurrentUser();

        if (!isMember(currentUser)) {
            throw new RuntimeException("Only members can view their quit plans");
        }

        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Member profile not found"));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMember(member);
        return quitPlans.stream()
                .map(quitPlanMapper::toResponse)
                .collect(Collectors.toList());
    }


    public List<QuitPlanResponse> getQuitPlansForCurrentCoach() {
        User currentUser = getCurrentUser();

        if (!isCoach(currentUser)) {
            throw new RuntimeException("Only coaches can view their created quit plans");
        }

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        List<QuitPlan> quitPlans = quitPlanRepository.findByCoach(coach);
        return quitPlans.stream()
                .map(quitPlanMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public QuitPlanStageResponse updateQuitPlanStage(Long stageId, QuitPlanStageRequest request) {
        User currentUser = getCurrentUser();

        QuitPlanStage stage = quitPlanStageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Quit plan stage not found"));

        QuitPlan quitPlan = stage.getQuitPlan();

        // Check if the current user is the coach or member of the quit plan
        if ((isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) ||
            (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId()))) {

            if (request.getStartDate() != null && request.getEndDate() != null) {
                if (request.getStartDate().isAfter(request.getEndDate())) {
                    throw new RuntimeException("Start date cannot be after end date.");
                }
            }

            Integer currentStageNumber = stage.getStageNumber();

            // Nếu không phải stage đầu tiên thì check phải lớn hơn endDate stage trước đó
            if (currentStageNumber != null && currentStageNumber > 1) {
                int previousStageNumber = currentStageNumber - 1;

                // Tìm stage trước đó trong cùng plan
                QuitPlanStage previousStage = quitPlan.getStages().stream()
                        .filter(s -> s.getStageNumber() != null && s.getStageNumber().equals(previousStageNumber))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Previous stage not found."));

                if (previousStage.getEndDate() != null && request.getStartDate() != null) {
                    if (!request.getStartDate().isAfter(previousStage.getEndDate())) {
                        throw new RuntimeException(
                                "Start date of this stage must be after end date of stage " + previousStageNumber);
                    }
                }
            }

            stage.setStartDate(request.getStartDate());
            stage.setEndDate(request.getEndDate());
            stage.setTargetCigaretteCount(request.getTargetCigaretteCount());
            stage.setAdvice(request.getAdvice());
            stage.setStatus(QuitPlanStageStatus.active);

            QuitPlanStage updatedStage = quitPlanStageRepository.save(stage);
            return quitPlanMapper.toStageResponse(updatedStage);
        } else {
            throw new RuntimeException("You don't have permission to update this quit plan stage");
        }
    }

    @Transactional
    public QuitPlanStageResponse addQuitPlanStage(Long quitPlanId, QuitPlanStageRequest request) {
        User currentUser = getCurrentUser();

        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new RuntimeException("Quit plan not found"));
        quitPlan.setTotalStages(quitPlan.getTotalStages() + 1);

        // Check if the current user is the coach or member of the quit plan
        if ((isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) ||
            (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId()))) {

            List<QuitPlanStage> existingStages = quitPlan.getStages();
            int nextStageNumber = (existingStages == null ? 0 : existingStages.size()) + 1;

            if (request.getStartDate() != null && request.getEndDate() != null) {
                if (request.getStartDate().isAfter(request.getEndDate())) {
                    throw new RuntimeException("Start date cannot be after end date.");
                }
            }

            if (nextStageNumber > 1) {
                int previousStageNumber = nextStageNumber - 1;

                QuitPlanStage previousStage = existingStages.stream()
                        .filter(s -> s.getStageNumber() != null && s.getStageNumber().equals(previousStageNumber))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Previous stage not found."));

                if (previousStage.getEndDate() != null && request.getStartDate() != null) {
                    if (!request.getStartDate().isAfter(previousStage.getEndDate())) {
                        throw new RuntimeException(
                                "Start date of stage " + nextStageNumber +
                                        " must be after end date of stage " + previousStageNumber + ".");
                    }
                }
            }

            QuitPlanStage stage = new QuitPlanStage();
            stage.setQuitPlan(quitPlan);
            stage.setStageNumber(nextStageNumber);
            stage.setStartDate(request.getStartDate());
            stage.setEndDate(request.getEndDate());
            stage.setTargetCigaretteCount(request.getTargetCigaretteCount());
            stage.setAdvice(request.getAdvice());
            stage.setStatus(QuitPlanStageStatus.active);

            QuitPlanStage savedStage = quitPlanStageRepository.save(stage);
            return quitPlanMapper.toStageResponse(savedStage);
        } else {
            throw new RuntimeException("You don't have permission to add a stage to this quit plan");
        }
    }

    @Transactional
    public void deleteQuitPlanStage(Long stageId) {
        User currentUser = getCurrentUser();

        QuitPlanStage stage = quitPlanStageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Quit plan stage not found"));

        QuitPlan quitPlan = stage.getQuitPlan();
        quitPlan.setTotalStages(quitPlan.getTotalStages() - 1);

        // Check if the current user is the coach or member of the quit plan
        if ((isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) ||
            (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId()))) {

            quitPlanStageRepository.delete(stage);
        } else {
            throw new RuntimeException("You don't have permission to delete this quit plan stage");
        }
    }

    public QuitPlanStageResponse getQuitPlanStageById(Long stageId) {
        User currentUser = getCurrentUser();

        QuitPlanStage stage = quitPlanStageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Quit plan stage not found"));

        QuitPlan quitPlan = stage.getQuitPlan();

        if ((isCoach(currentUser) && quitPlan.getCoach().getUser().getId().equals(currentUser.getId())) ||
                (isMember(currentUser) && quitPlan.getMember().getUser().getId().equals(currentUser.getId()))) {

            return quitPlanMapper.toStageResponse(stage);

        } else {
            throw new RuntimeException("You don't have permission to view this quit plan stage");
        }
    }

    public List<QuitPlanStageResponse> getAllQuitPlanStagesForCurrentUser() {
        User currentUser = getCurrentUser();

        List<QuitPlan> quitPlans;

        if (isCoach(currentUser)) {
            Coach coach = coachRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Coach profile not found"));
            quitPlans = quitPlanRepository.findByCoach(coach);

        } else if (isMember(currentUser)) {
            Member member = memberRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Member profile not found"));
            quitPlans = quitPlanRepository.findByMember(member);

        } else {
            throw new RuntimeException("Unsupported role for fetching Quit Plan Stages");
        }

        List<QuitPlanStage> allStages = quitPlans.stream()
                .flatMap(plan -> plan.getStages().stream())
                .collect(Collectors.toList());

        return allStages.stream()
                .map(quitPlanMapper::toStageResponse)
                .collect(Collectors.toList());
    }




}

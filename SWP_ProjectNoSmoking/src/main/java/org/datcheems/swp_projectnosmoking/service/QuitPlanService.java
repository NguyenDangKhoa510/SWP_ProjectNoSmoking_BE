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

    /**
     * Get the current authenticated user
     * @return The current user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Check if the current user is a coach
     * @param user The user to check
     * @return True if the user is a coach, false otherwise
     */
    private boolean isCoach(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> Role.RoleName.COACH.equals(role.getName()));
    }


    /**
     * Check if the current user is a member
     * @param user The user to check
     * @return True if the user is a member, false otherwise
     */
    private boolean isMember(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> Role.RoleName.MEMBER.equals(role.getName()));
    }


    @Transactional
    public QuitPlanResponse createQuitPlan(QuitPlanRequest request) {
        User currentUser = getCurrentUser();

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past");
        }


        if (!isCoach(currentUser)) {
            throw new RuntimeException("Only Coaches can create Quit Plans");
        }

        Coach coach = coachRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Coach profile not found"));

        // Find user by username or email
        String memberIdentifier = request.getMemberIdentifier();
        User memberUser;

        // Try to find by username first, then by email if not found
        memberUser = userRepository.findByUsername(memberIdentifier)
                .orElseGet(() -> userRepository.findByEmail(memberIdentifier)
                        .orElseThrow(() -> new RuntimeException("User not found with identifier: " + memberIdentifier)));

        // Find the member associated with the user
        Member member = memberRepository.findByUserId(memberUser.getId())
                .orElseThrow(() -> new RuntimeException("Member profile not found for user: " + memberIdentifier));

        QuitPlan plan = new QuitPlan();
        plan.setMember(member);
        plan.setCoach(coach);
        plan.setStartDate(request.getStartDate());
        plan.setGoal(request.getGoal());

        QuitPlan savedPlan = quitPlanRepository.save(plan);

        // Auto-generate QuitPlanStages: 7, 14, 21 days
        List<QuitPlanStage> stages = new ArrayList<>();
        int[] days = {7, 14, 21};

        for (int day : days) {
            QuitPlanStage stage = new QuitPlanStage();
            stage.setQuitPlan(savedPlan);
            stage.setDay(day);
            stage.setTargetDate(request.getStartDate().plusDays(day));
            stage.setDescription("Target day " + day + " of quit plan");
            stages.add(stage);
        }

        quitPlanStageRepository.saveAll(stages);
        savedPlan.setStages(stages);

        return quitPlanMapper.toResponse(savedPlan);
    }

    /**
     * Get a quit plan by ID
     * @param quitPlanId The ID of the quit plan
     * @return The quit plan response
     */
    public QuitPlanResponse getQuitPlanById(Long quitPlanId) {
        User currentUser = getCurrentUser();
        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new RuntimeException("Quit plan not found"));

        // Check if the current user is the coach or member of the quit plan
        if (isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) {
            return quitPlanMapper.toResponse(quitPlan);
        } else if (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId())) {
            return quitPlanMapper.toResponse(quitPlan);
        } else {
            throw new RuntimeException("You don't have permission to view this quit plan");
        }
    }

    /**
     * Get all quit plans for the current member
     * @return List of quit plan responses
     */
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

    /**
     * Get all quit plans created by the current coach
     * @return List of quit plan responses
     */
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

    /**
     * Update a quit plan stage
     * @param stageId The ID of the stage to update
     * @param request The update request
     * @return The updated stage response
     */
    @Transactional
    public QuitPlanStageResponse updateQuitPlanStage(Long stageId, QuitPlanStageRequest request) {
        User currentUser = getCurrentUser();

        QuitPlanStage stage = quitPlanStageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Quit plan stage not found"));

        QuitPlan quitPlan = stage.getQuitPlan();

        // Check if the current user is the coach or member of the quit plan
        if ((isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) ||
            (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId()))) {

            stage.setDay(request.getDay());
            stage.setDescription(request.getDescription());
            stage.setTargetDate(request.getTargetDate());

            QuitPlanStage updatedStage = quitPlanStageRepository.save(stage);
            return quitPlanMapper.toStageResponse(updatedStage);
        } else {
            throw new RuntimeException("You don't have permission to update this quit plan stage");
        }
    }

    /**
     * Add a new stage to a quit plan
     * @param quitPlanId The ID of the quit plan
     * @param request The stage request
     * @return The created stage response
     */
    @Transactional
    public QuitPlanStageResponse addQuitPlanStage(Long quitPlanId, QuitPlanStageRequest request) {
        User currentUser = getCurrentUser();

        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new RuntimeException("Quit plan not found"));

        // Check if the current user is the coach or member of the quit plan
        if ((isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) ||
            (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId()))) {

            QuitPlanStage stage = new QuitPlanStage();
            stage.setQuitPlan(quitPlan);
            stage.setDay(request.getDay());
            stage.setDescription(request.getDescription());
            stage.setTargetDate(request.getTargetDate());

            QuitPlanStage savedStage = quitPlanStageRepository.save(stage);
            return quitPlanMapper.toStageResponse(savedStage);
        } else {
            throw new RuntimeException("You don't have permission to add a stage to this quit plan");
        }
    }

    /**
     * Delete a quit plan stage
     * @param stageId The ID of the stage to delete
     */
    @Transactional
    public void deleteQuitPlanStage(Long stageId) {
        User currentUser = getCurrentUser();

        QuitPlanStage stage = quitPlanStageRepository.findById(stageId)
                .orElseThrow(() -> new RuntimeException("Quit plan stage not found"));

        QuitPlan quitPlan = stage.getQuitPlan();

        // Check if the current user is the coach or member of the quit plan
        if ((isCoach(currentUser) && quitPlan.getCoach().getUserId().equals(currentUser.getId())) ||
            (isMember(currentUser) && quitPlan.getMember().getUserId().equals(currentUser.getId()))) {

            quitPlanStageRepository.delete(stage);
        } else {
            throw new RuntimeException("You don't have permission to delete this quit plan stage");
        }
    }
}

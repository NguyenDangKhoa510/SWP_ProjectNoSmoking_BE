package org.datcheems.swp_projectnosmoking.service;

import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanResponse;
import org.datcheems.swp_projectnosmoking.entity.QuitPlan;
import org.datcheems.swp_projectnosmoking.repository.QuitPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuitPlanService {

    private final QuitPlanRepository quitPlanRepository;

    public QuitPlanService(QuitPlanRepository quitPlanRepository) {
        this.quitPlanRepository = quitPlanRepository;
    }

    public QuitPlanResponse createQuitPlan(QuitPlanRequest request) {
        QuitPlan plan = new QuitPlan();
        plan.setMemberId(request.getMemberId());
        plan.setCoachId(request.getCoachId());
        plan.setSelectionId(request.getSelectionId());
        plan.setGoalDescription(request.getGoalDescription());
        plan.setStatus(QuitPlan.PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());

        QuitPlan saved = quitPlanRepository.save(plan);

        QuitPlanResponse response = new QuitPlanResponse();
        response.setId(saved.getId());
        response.setMemberId(saved.getMemberId());
        response.setCoachId(saved.getCoachId());
        response.setSelectionId(saved.getSelectionId());
        response.setGoalDescription(saved.getGoalDescription());
        response.setStatus(saved.getStatus().name());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        return response;
    }
}

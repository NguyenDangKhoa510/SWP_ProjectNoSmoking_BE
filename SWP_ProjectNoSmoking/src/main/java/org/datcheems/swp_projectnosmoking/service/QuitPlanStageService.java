package org.datcheems.swp_projectnosmoking.service;

import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanStageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanStageResponse;
import org.datcheems.swp_projectnosmoking.entity.QuitPlanStage;
import org.datcheems.swp_projectnosmoking.repository.QuitPlanStageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuitPlanStageService {

    private final QuitPlanStageRepository stageRepository;

    public QuitPlanStageService(QuitPlanStageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    public void createStage(QuitPlanStageRequest request) {
        QuitPlanStage stage = new QuitPlanStage();
        stage.setQuitPlanId(request.getQuitPlanId());
        stage.setStageNumber(request.getStageNumber());
        stage.setMilestoneDate(request.getMilestoneDate());
        stage.setAdvice(request.getAdvice());
        stage.setStatus(QuitPlanStage.StageStatus.ACTIVE);

        stageRepository.save(stage);
    }

    public List<QuitPlanStageResponse> getStagesByPlanId(Integer planId) {
        List<QuitPlanStage> stages = stageRepository.findByQuitPlanId(planId);

        return stages.stream().map(stage -> {
            QuitPlanStageResponse response = new QuitPlanStageResponse();
            response.setId(stage.getId());
            response.setQuitPlanId(stage.getQuitPlanId());
            response.setStageNumber(stage.getStageNumber());
            response.setMilestoneDate(stage.getMilestoneDate());
            response.setStatus(stage.getStatus().name());
            response.setAdvice(stage.getAdvice());
            return response;
        }).collect(Collectors.toList());
    }
}

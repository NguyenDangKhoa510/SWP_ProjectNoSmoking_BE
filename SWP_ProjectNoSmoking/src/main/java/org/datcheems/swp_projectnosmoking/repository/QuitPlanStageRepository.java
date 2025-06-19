package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.QuitPlanStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuitPlanStageRepository extends JpaRepository<QuitPlanStage, Integer> {
    List<QuitPlanStage> findByQuitPlanId(Integer quitPlanId);
}

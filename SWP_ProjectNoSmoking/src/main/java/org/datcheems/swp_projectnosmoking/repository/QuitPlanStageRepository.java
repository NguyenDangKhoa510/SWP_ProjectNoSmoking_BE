package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.QuitPlanStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuitPlanStageRepository extends JpaRepository<QuitPlanStage, Long> {
    List<QuitPlanStage> findByQuitPlan_Member(Member member);
}

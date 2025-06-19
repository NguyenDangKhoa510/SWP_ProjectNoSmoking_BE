package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer> {
    List<QuitPlan> findByMemberId(Integer memberId);
    List<QuitPlan> findByCoachId(Integer coachId);
}

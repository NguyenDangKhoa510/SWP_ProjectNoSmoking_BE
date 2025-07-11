package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuitPlanRepository extends JpaRepository<QuitPlan, Long> {
    List<QuitPlan> findByMember(Member member);
    List<QuitPlan> findByCoach(Coach coach);

    long countByCoach(Coach coach);

}

package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.CoachReview;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachReviewRepository extends JpaRepository<CoachReview, Long> {
    List<CoachReview> findByCoach(Coach coach);
    boolean existsByCoachAndMember(Coach coach, Member member);
    List<CoachReview> findByMember(Member member);
}

package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.CoachReview;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoachReviewRepository extends JpaRepository<CoachReview, Long> {
    List<CoachReview> findByCoach(Coach coach);
    boolean existsByCoachAndMember(Coach coach, Member member);
    List<CoachReview> findByMember(Member member);

    long countByCoach(Coach coach);

    @Query("SELECT AVG(r.rating) FROM CoachReview r WHERE r.coach.user.id = :coachId")
    Double findAverageRatingByCoachId(@Param("coachId") Long coachId);

    @Query("SELECT r.coach.userId, r.coach.user.fullName, AVG(r.rating) AS avgRating " +
            "FROM CoachReview r GROUP BY r.coach.userId, r.coach.user.fullName " +
            "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedCoaches(Pageable pageable);



}

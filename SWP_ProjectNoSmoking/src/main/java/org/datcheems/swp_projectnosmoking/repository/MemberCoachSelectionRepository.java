package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCoachSelectionRepository extends JpaRepository<MemberCoachSelection, Long> {
    boolean existsByMemberAndCoach(Member member, Coach coach);

    boolean existsByMember(Member member);

    Optional<MemberCoachSelection> findByMemberAndCoach(Member member, Coach coach);

    List<MemberCoachSelection> findByCoach(Coach coach);
    long countByCoach(Coach coach);

    Optional<MemberCoachSelection> findFirstByMember_User_Id(Long userId);

    boolean existsByMember_UserIdAndCoach_UserId(Long memberId, Long coachId);

    @Query("SELECT mcs FROM MemberCoachSelection mcs WHERE mcs.coach.userId = :coachId")
    List<MemberCoachSelection> findByCoachId(@Param("coachId") Long coachId);

    Optional<MemberCoachSelection> findByMember(Member member);

}

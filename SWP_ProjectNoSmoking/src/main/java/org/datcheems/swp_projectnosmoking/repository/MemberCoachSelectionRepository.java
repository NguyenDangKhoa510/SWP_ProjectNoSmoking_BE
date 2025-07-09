package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Coach;
import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MemberCoachSelection;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCoachSelectionRepository extends JpaRepository<MemberCoachSelection, Long> {
    boolean existsByMemberAndCoach(Member member, Coach coach);

    Optional<MemberCoachSelection> findByMemberAndCoach(Member member, Coach coach);
    List<MemberCoachSelection> findByCoach(Coach coach);


    List<MemberCoachSelection> findByCoach(Coach coach);

}

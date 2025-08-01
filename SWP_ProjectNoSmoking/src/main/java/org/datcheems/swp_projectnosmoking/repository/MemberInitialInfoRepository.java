package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.MemberInitialInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberInitialInfoRepository extends JpaRepository<MemberInitialInfo, Long> {
    Optional<MemberInitialInfo> findByMember(Member member);
    List<MemberInitialInfo> findByMemberIn(List<Member> members);
}

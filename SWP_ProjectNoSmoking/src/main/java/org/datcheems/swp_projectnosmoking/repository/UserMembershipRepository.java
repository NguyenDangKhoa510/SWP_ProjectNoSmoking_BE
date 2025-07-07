package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.User_Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMembershipRepository extends JpaRepository<User_Membership, Long> {
    List<User_Membership> findByMember_UserId(Long userId);
}

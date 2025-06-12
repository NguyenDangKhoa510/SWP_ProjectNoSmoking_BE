package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.SmokingStatus;
import org.datcheems.swp_projectnosmoking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmokingStatusRepository extends JpaRepository<SmokingStatus,  Long> {
    Optional<SmokingStatus> findByUser(User user);

    boolean existsByUser(User user);
    // Custom query methods can be defined here if needed
    // For example, to find by userId or other criteria
    // List<SmokingStatus> findByUserId(Long userId);
}

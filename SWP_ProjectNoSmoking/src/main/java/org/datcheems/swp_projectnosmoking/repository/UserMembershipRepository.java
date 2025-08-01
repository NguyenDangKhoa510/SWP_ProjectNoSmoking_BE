package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    List<UserMembership> findByMember_UserId(Long userId);
    boolean existsByTransactionId(String transactionId);

    // Find memberships created in a specific month and year
    @Query("SELECT um FROM UserMembership um WHERE YEAR(um.startDate) = :year AND MONTH(um.startDate) = :month")
    List<UserMembership> findByStartDateYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Find memberships created in a specific year
    @Query("SELECT um FROM UserMembership um WHERE YEAR(um.startDate) = :year")
    List<UserMembership> findByStartDateYear(@Param("year") int year);

    // Find memberships created between two dates
    List<UserMembership> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // Count memberships created in a specific month and year
    @Query("SELECT COUNT(um) FROM UserMembership um WHERE YEAR(um.startDate) = :year AND MONTH(um.startDate) = :month")
    Long countByStartDateYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Count memberships created in a specific year
    @Query("SELECT COUNT(um) FROM UserMembership um WHERE YEAR(um.startDate) = :year")
    Long countByStartDateYear(@Param("year") int year);
}

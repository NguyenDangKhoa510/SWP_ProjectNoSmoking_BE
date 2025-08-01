package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Member;
import org.datcheems.swp_projectnosmoking.entity.SmokingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SmokingLogRepository extends JpaRepository<SmokingLog, Long> {
    
    List<SmokingLog> findByMemberOrderByLogDateDesc(Member member);
    
    Optional<SmokingLog> findByMemberAndLogDate(Member member, LocalDate logDate);
    
    @Query("SELECT sl FROM SmokingLog sl WHERE sl.member = :member AND sl.logDate < :date ORDER BY sl.logDate DESC")
    List<SmokingLog> findPreviousLogs(@Param("member") Member member, @Param("date") LocalDate date);
    
    @Query("SELECT sl FROM SmokingLog sl WHERE sl.member = :member AND sl.logDate = :date")
    Optional<SmokingLog> findTodayLog(@Param("member") Member member, @Param("date") LocalDate date);

    @Query(value = """
    SELECT * FROM members m
    WHERE m.user_id NOT IN (
        SELECT sl.member_id FROM smoking_logs sl WHERE sl.log_date = :date
    )
""", nativeQuery = true)
    List<Member> findMembersWithoutLogForDate(@Param("date") LocalDate date);




    @Query(value = """
    SELECT TOP 5 
        m.user_id, 
        u.full_name, 
        SUM(s.smoke_count) AS total_smoke
    FROM members m
    JOIN smoking_logs s ON m.user_id = s.member_id
    JOIN tbl_User u ON u.id = m.user_id
    WHERE s.log_date BETWEEN :startDate AND :endDate
    GROUP BY m.user_id, u.full_name
    ORDER BY total_smoke ASC
""", nativeQuery = true)
    List<Object[]> findTop5MembersWithLeastSmoking(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );







}
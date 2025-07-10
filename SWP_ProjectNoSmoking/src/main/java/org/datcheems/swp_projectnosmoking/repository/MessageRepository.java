package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySelection_SelectionId(Long selectionId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.selection.selectionId = :selectionId AND m.senderType = 'MEMBER'")
    int markAllMessagesAsRead(@Param("selectionId") Long selectionId);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.selection.selectionId = :selectionId " +
            "AND m.senderType = 'MEMBER' " +
            "AND m.isRead = false")
    Long countUnreadMessages(@Param("selectionId") Long selectionId);

}

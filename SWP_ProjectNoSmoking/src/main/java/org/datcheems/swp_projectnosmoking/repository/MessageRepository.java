package org.datcheems.swp_projectnosmoking.repository;

import org.datcheems.swp_projectnosmoking.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySelection_SelectionId(Long selectionId);

    Message findFirstBySelection_SelectionIdOrderBySentAtDesc(Long selectionId);
}

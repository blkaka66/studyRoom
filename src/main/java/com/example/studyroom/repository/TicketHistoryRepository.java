package com.example.studyroom.repository;

import com.example.studyroom.model.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistoryEntity, Long> {
    TicketHistoryEntity findByShopIdAndUserId(Long shopId, Long userId);
}

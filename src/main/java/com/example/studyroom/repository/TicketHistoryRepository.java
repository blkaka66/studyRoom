package com.example.studyroom.repository;

import com.example.studyroom.model.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistoryEntity, Long> {

    List<TicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);

}

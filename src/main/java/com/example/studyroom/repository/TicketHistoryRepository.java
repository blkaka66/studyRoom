package com.example.studyroom.repository;

import com.example.studyroom.model.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistoryEntity, Long> {

    @Query("SELECT the From TicketHistoryEntity the WHERE the.ticket.shop.id=?1 and the.member.id=?2")
    List<TicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);

}

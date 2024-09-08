package com.example.studyroom.repository;

import com.example.studyroom.model.PeriodTicketHistoryEntity;
import com.example.studyroom.model.TimeTicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TimeTicketHistoryRepository extends JpaRepository<TimeTicketHistoryEntity, Long> {

    @Query("SELECT the From TimeTicketHistoryEntity the WHERE the.ticket.shop.id=?1 and the.member.id=?2")
    List<TimeTicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);
}

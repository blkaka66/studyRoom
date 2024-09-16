package com.example.studyroom.repository;

import com.example.studyroom.model.PeriodTicketHistoryEntity;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PeriodTicketHistoryRepository extends TicketHistoryRepository<PeriodTicketHistoryEntity> {

    @Query("SELECT the From PeriodTicketHistoryEntity the WHERE the.shop=?1 and the.member.id=?2")
    List<PeriodTicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);


}

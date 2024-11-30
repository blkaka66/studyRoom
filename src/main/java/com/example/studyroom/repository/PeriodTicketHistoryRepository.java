package com.example.studyroom.repository;

import com.example.studyroom.model.PeriodTicketHistoryEntity;

import com.example.studyroom.model.TimeTicketHistoryEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PeriodTicketHistoryRepository extends TicketHistoryRepository<PeriodTicketHistoryEntity> {

    @Query("SELECT the From PeriodTicketHistoryEntity the WHERE the.shop=?1 and the.member.id=?2")
    List<PeriodTicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);

    List<PeriodTicketHistoryEntity> findByShop_IdAndMember_Id(Long shopId, Long userId);
}

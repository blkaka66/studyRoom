package com.example.studyroom.repository;

import com.example.studyroom.model.TimeTicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TimeTicketHistoryRepository extends TicketHistoryRepository<TimeTicketHistoryEntity> {

    @Query("SELECT the From TimeTicketHistoryEntity the WHERE the.shop.id=?1 and the.member.id=?2")
    List<TimeTicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);


    List<TimeTicketHistoryEntity> findByShop_IdAndMember_Id(Long shopId, Long userId);
}

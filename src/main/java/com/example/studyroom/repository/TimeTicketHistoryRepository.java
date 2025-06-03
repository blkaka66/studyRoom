package com.example.studyroom.repository;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.PeriodTicketHistoryEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.TimeTicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface TimeTicketHistoryRepository extends TicketHistoryRepository<TimeTicketHistoryEntity> {

//    @Query("SELECT the From TimeTicketHistoryEntity the WHERE the.shop.id=?1 and the.member.id=?2")
//    List<TimeTicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);

    List<TimeTicketHistoryEntity> findByShopAndPaymentDateBetween(ShopEntity shop, OffsetDateTime paymentDate, OffsetDateTime paymentDate2);

    List<TimeTicketHistoryEntity> findByShopAndMemberAndPaymentDateBetween(ShopEntity shop, MemberEntity member, OffsetDateTime startDate, OffsetDateTime endDate);
}

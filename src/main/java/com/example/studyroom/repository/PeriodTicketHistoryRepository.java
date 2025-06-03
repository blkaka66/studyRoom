package com.example.studyroom.repository;

import com.example.studyroom.model.*;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface PeriodTicketHistoryRepository extends TicketHistoryRepository<PeriodTicketHistoryEntity> {

//    @Query("SELECT the From PeriodTicketHistoryEntity the WHERE the.shop=?1 and the.member.id=?2")
//    List<PeriodTicketHistoryEntity> findByShopIdAndUserIdAndExpiredFalse(Long shopId, Long userId);

    List<PeriodTicketHistoryEntity> findByShopAndPaymentDateBetween(ShopEntity shop, OffsetDateTime paymentDate, OffsetDateTime paymentDate2);

    List<PeriodTicketHistoryEntity> findByShopAndMemberAndPaymentDateBetween(ShopEntity shop, MemberEntity member, OffsetDateTime startDate, OffsetDateTime endDate);
}

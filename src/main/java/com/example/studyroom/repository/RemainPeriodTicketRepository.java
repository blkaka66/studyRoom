package com.example.studyroom.repository;


import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.RemainPeriodTicketEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

//기간권
public interface RemainPeriodTicketRepository extends JpaRepository<RemainPeriodTicketEntity, Long> {

    //Optional<RemainPeriodTicketEntity> findByShopIdAndMemberId(Long shopId, Long userId);

//
//    Optional<RemainPeriodTicketEntity> findByShopIdAndMemberIdAndEndDateAfterAndExpiresAtAfter(
//            Long shopId,
//            Long memberId,
//            OffsetDateTime endDateBefore,
//            OffsetDateTime expiresAtBefore
//    );


    Optional<RemainPeriodTicketEntity> findByShopAndMemberAndEndDateAfterAndExpiresAtAfter(
            ShopEntity shop,
            MemberEntity member,
            OffsetDateTime endDateBefore,
            OffsetDateTime expiresAtBefore
    );

    // Optional<RemainPeriodTicketEntity> findByMemberId(Long userId);

    void deleteByShopAndMember(ShopEntity shop, MemberEntity member);

    @Modifying
    @Transactional
    @Query("DELETE FROM RemainPeriodTicketEntity r WHERE r.endDate < :now")
    void deleteExpiredTickets(OffsetDateTime now);


}

package com.example.studyroom.repository;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.RemainPeriodTicketEntity;
import com.example.studyroom.model.RemainTimeTicketEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

//시간권
public interface RemainTimeTicketRepository extends JpaRepository<RemainTimeTicketEntity, Long> {
    Optional<RemainTimeTicketEntity> findByShopIdAndMemberId(Long shopId, Long userId);

    //Optional<RemainTimeTicketEntity> findByShopIdAndMemberIdAndExpiresAtBefore(Long shopId, Long userId, OffsetDateTime now);

//    Optional<RemainTimeTicketEntity> findByShopIdAndMemberIdAndExpiresAtAfter(
//            Long shopId,
//            Long memberId,
//            OffsetDateTime expiresAtBefore
//    );

    Optional<RemainTimeTicketEntity> findByShopAndMemberAndExpiresAtAfter(
            ShopEntity shop,
            MemberEntity member,
            OffsetDateTime expiresAtBefore
    );


    @Transactional
    void deleteByShopAndMember(ShopEntity shop, MemberEntity member);


    Optional<RemainTimeTicketEntity> findByMember(MemberEntity member); // memberId로 시간권을 찾는 메서드

}

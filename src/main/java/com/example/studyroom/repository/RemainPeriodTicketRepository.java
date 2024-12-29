package com.example.studyroom.repository;


import com.example.studyroom.model.RemainPeriodTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

//기간권
public interface RemainPeriodTicketRepository extends JpaRepository<RemainPeriodTicketEntity, Long> {

    Optional<RemainPeriodTicketEntity> findByShopIdAndMemberId(Long shopId, Long userId);

    void deleteByShopIdAndMemberId(Long shopId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RemainPeriodTicketEntity r WHERE r.endDate < :now")
    void deleteExpiredTickets(OffsetDateTime now);
}

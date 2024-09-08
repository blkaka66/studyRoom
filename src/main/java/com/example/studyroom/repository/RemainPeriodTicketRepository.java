package com.example.studyroom.repository;


import com.example.studyroom.model.RemainPeriodTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//기간권
public interface RemainPeriodTicketRepository extends JpaRepository<RemainPeriodTicketEntity, Long> {

    Optional<RemainPeriodTicketEntity> findByShopIdAndMemberId(Long shopId, Long userId);

    void deleteByShopIdAndMemberId(Long shopId, Long userId);
}

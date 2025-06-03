package com.example.studyroom.repository;

import com.example.studyroom.model.PeriodTicketEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodTicketRepository extends JpaRepository<PeriodTicketEntity, Long> {
    List<PeriodTicketEntity> findByShop(ShopEntity shop);
}

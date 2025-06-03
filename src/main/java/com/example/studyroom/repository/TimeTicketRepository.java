package com.example.studyroom.repository;

import com.example.studyroom.model.PeriodTicketEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.TimeTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeTicketRepository extends JpaRepository<TimeTicketEntity, Long> {
    List<TimeTicketEntity> findByShop(ShopEntity shop);
}

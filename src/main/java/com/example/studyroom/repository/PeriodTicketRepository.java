package com.example.studyroom.repository;

import com.example.studyroom.model.PeriodTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodTicketRepository  extends JpaRepository<PeriodTicketEntity, Long> {
   List<PeriodTicketEntity> findByShopId(Long shopId);
}

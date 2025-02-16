package com.example.studyroom.repository;

import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TicketHistoryRepository <T extends TicketHistoryEntity> extends JpaRepository<T, Long> {


}

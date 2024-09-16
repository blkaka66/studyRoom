package com.example.studyroom.repository;

import com.example.studyroom.model.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository <T extends TicketHistoryEntity> extends JpaRepository<T, Long> {
    

}

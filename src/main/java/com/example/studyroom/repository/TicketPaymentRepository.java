package com.example.studyroom.repository;

import com.example.studyroom.model.TicketEntity;
import com.example.studyroom.model.TicketPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPaymentRepository extends JpaRepository<TicketPaymentEntity, Long> {
    //
}

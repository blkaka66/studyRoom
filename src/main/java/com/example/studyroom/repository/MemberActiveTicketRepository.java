package com.example.studyroom.repository;

import com.example.studyroom.model.MemberActiveTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberActiveTicketRepository extends JpaRepository<MemberActiveTicketEntity, Long> {
    @Query("SELECT m FROM MemberActiveTicketEntity m WHERE m.member.id = :customerId AND m.ticketHistory.expired = false ORDER BY m.ticketHistory.id DESC")
    List<MemberActiveTicketEntity> findActiveTicketsByCustomerId(@Param("customerId") Long customerId);
}

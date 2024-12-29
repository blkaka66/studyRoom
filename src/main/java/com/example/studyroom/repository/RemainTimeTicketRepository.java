package com.example.studyroom.repository;

import com.example.studyroom.model.RemainPeriodTicketEntity;
import com.example.studyroom.model.RemainTimeTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
//시간권
public interface RemainTimeTicketRepository extends JpaRepository<RemainTimeTicketEntity, Long> {
    Optional<RemainTimeTicketEntity> findByShopIdAndMemberId(Long shopId, Long userId);
    @Transactional
    void deleteByShopIdAndMemberId(Long shopId, Long userId);


    Optional<RemainTimeTicketEntity> findByMemberId(Long memberId); // memberId로 시간권을 찾는 메서드

}

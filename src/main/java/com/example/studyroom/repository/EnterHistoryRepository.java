package com.example.studyroom.repository;

import com.example.studyroom.model.EnterHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnterHistoryRepository extends JpaRepository<EnterHistoryEntity, Long> {


    // 특정 회원의 현재 활성화된 입장 기록을 조회하는 메서드
    // 조건: customerId가 일치하고, expiredTime이 현재 시간 이전이며, closeTime이 null인 레코드
    @Query("SELECT e FROM EnterHistoryEntity e WHERE e.customerId = :customerId AND e.expiredTime < CURRENT_TIMESTAMP AND e.closeTime IS NULL")
    EnterHistoryEntity findActiveByCustomerId(@Param("customerId") Long customerId);//쿼리조건 추가할거면 이름에 active라고 추가해야함


}

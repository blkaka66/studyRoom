package com.example.studyroom.repository.statistics;

import com.example.studyroom.model.statistics.SeatIdUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface SeatIdUsageRepository extends JpaRepository<SeatIdUsageEntity, Long> {
    // 주어진 기간 내의 SeatIdUsageEntity 목록 조회
    // shopId와 기간에 맞는 SeatIdUsageEntity 목록을 조회하는 메서드
    List<SeatIdUsageEntity> findByShopIdAndUsageDateBetween(long shopId, LocalDate startDate, LocalDate endDate);
}

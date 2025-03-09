package com.example.studyroom.repository.statistics;

import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import com.example.studyroom.model.statistics.ShopUsageDailyEntity;
import com.example.studyroom.model.statistics.ShopUsageHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShopUsageDailyRepository extends JpaRepository<ShopUsageDailyEntity, Long> {
    // shopId와 기간에 맞는 ShopDailyPaymentEntity 목록을 조회
    List<ShopUsageDailyEntity> findByShopIdAndUsageDateBetween(long shopId, LocalDate startDate, LocalDate endDate);
}

package com.example.studyroom.repository.statistics;

import com.example.studyroom.model.statistics.CustomerChangeStatsEntity;
import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CustomerChangeStatsRepository extends JpaRepository<CustomerChangeStatsEntity, Long> {

    List<CustomerChangeStatsEntity> findByShopIdAndUsageDateBetween(long shopId, LocalDate startDate, LocalDate endDate);
}

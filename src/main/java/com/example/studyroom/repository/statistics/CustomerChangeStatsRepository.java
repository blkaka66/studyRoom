package com.example.studyroom.repository.statistics;

import com.example.studyroom.model.statistics.CustomerChangeStatsEntity;
import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerChangeStatsRepository extends JpaRepository<CustomerChangeStatsEntity, Long> {

    List<CustomerChangeStatsEntity> findByShopIdAndYearBetweenAndMonthBetweenAndDayBetween(
            long shopId,
            int startYear, int endYear,
            int startMonth, int endMonth,
            int startDay, int endDay
    );
}

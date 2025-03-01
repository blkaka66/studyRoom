package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopDailyPaymentRepository extends JpaRepository<ShopDailyPaymentEntity, Long> {


    List<ShopDailyPaymentEntity> findByShopIdAndYearBetweenAndMonthBetweenAndDayBetween(
            long shopId,
            int startYear, int endYear,
            int startMonth, int endMonth,
            int startDay, int endDay
    );
}

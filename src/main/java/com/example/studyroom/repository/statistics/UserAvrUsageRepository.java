package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import com.example.studyroom.model.statistics.UserAvrUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAvrUsageRepository extends JpaRepository<UserAvrUsageEntity, Long> {

    List<UserAvrUsageEntity> findByShopIdAndYearBetweenAndMonthBetweenAndDayBetween(
            long shopId,
            int startYear, int endYear,
            int startMonth, int endMonth,
            int startDay, int endDay
    );
}

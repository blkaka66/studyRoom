package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import com.example.studyroom.model.statistics.UserAvrUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserAvrUsageRepository extends JpaRepository<UserAvrUsageEntity, Long> {

    List<UserAvrUsageEntity> findByShopIdAndUsageDateBetween(long shopId, LocalDate startDate, LocalDate endDate);
}

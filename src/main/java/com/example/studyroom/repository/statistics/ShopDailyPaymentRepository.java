package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ShopDailyPaymentRepository extends JpaRepository<ShopDailyPaymentEntity, Long> {






    List<ShopDailyPaymentEntity> findByShopIdAndUsageDateBetween(long shopId, LocalDate startDate, LocalDate endDate);

}

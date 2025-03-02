package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShopDailyPaymentRepository extends JpaRepository<ShopDailyPaymentEntity, Long> {


    List<ShopDailyPaymentEntity> findByShopIdAndYearBetweenAndMonthBetweenAndDayBetween(
            long shopId,
            int startYear, int endYear,
            int startMonth, int endMonth,
            int startDay, int endDay
    );
    
    @Query(value = "SELECT s FROM ShopDailyPaymentEntity s " +
            "WHERE s.shop.id = :shopId " +
            "AND s.date BETWEEN :startDate AND :endDate")
    List<ShopDailyPaymentEntity> findByShopIdAndStartDateAndEndDate(long shopId, String startDate, String endDate);

    List<ShopDailyPaymentEntity> findByShopIdAndDateBetween(long shopId, String startDate, String endDate);

}

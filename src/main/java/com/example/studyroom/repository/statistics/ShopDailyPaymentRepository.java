package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopDailyPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopDailyPaymentRepository extends JpaRepository<ShopDailyPaymentEntity, Long> {
}

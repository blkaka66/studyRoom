package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.ShopUsageHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopUsageHourlyRepository extends JpaRepository<ShopUsageHourlyEntity, Long> {
}

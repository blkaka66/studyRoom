package com.example.studyroom.repository.statistics;

import com.example.studyroom.model.statistics.ShopUsageDailyEntity;
import com.example.studyroom.model.statistics.ShopUsageHourlyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopUsageDailyRepository extends JpaRepository<ShopUsageDailyEntity, Long> {
}

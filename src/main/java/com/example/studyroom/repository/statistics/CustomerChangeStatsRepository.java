package com.example.studyroom.repository.statistics;

import com.example.studyroom.model.statistics.CustomerChangeStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerChangeStatsRepository extends JpaRepository<CustomerChangeStatsEntity, Long> {
}

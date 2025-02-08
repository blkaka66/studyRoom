package com.example.studyroom.repository.statistics;


import com.example.studyroom.model.statistics.UserAvrUsageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAvrUsageRepository extends JpaRepository<UserAvrUsageEntity, Long> {
}

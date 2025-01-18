package com.example.studyroom.repository;

import com.example.studyroom.model.AnnouncementEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    List<AnnouncementEntity> findByShopIdAndIsActiveTrue(Long shopId);

    Optional<AnnouncementEntity> findByIdAndIsActiveTrue(Long id);
}

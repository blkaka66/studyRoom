package com.example.studyroom.repository;

import com.example.studyroom.model.AnnouncementEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    List<AnnouncementEntity> findByShopId(Long shopId);

}

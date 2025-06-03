package com.example.studyroom.repository;

import com.example.studyroom.model.AnnouncementEntity;

import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Long> {
    @Query("SELECT A FROM AnnouncementEntity A WHERE (A.isActive = true ) AND A.shop = :shop")
    List<AnnouncementEntity> findByShopAndIsActiveTrue(@Param("shop") ShopEntity shop);


    @Query("SELECT A FROM AnnouncementEntity A WHERE (A.isActive = true ) AND A.id = :id")
    Optional<AnnouncementEntity> findByIdAndIsActiveTrue(@Param("id") Long id);
}

package com.example.studyroom.repository;


import com.example.studyroom.model.RoomEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    List<RoomEntity> findByShop(ShopEntity shop);

    // Optional<RoomEntity> findByName(String name);
}

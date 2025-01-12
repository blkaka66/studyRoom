package com.example.studyroom.repository;

import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    boolean existsByEmail(String email);
    ShopEntity findByEmailAndPassword(String email, String password);
    ShopEntity findByEmail(String email);
    ShopEntity findById(long Id);

}

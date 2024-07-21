package com.example.studyroom.repository;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    boolean existsByEmail(String email);
    ShopEntity findByemailAndpassword(String email, String password);
    ShopEntity findById(long Id);
}

package com.example.studyroom.repository;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    List<MemberEntity> findByShop(ShopEntity shop);
    MemberEntity findByNameAndPassword(String name, String password);
    MemberEntity findByPhoneAndPassword(String phone, String password);
}

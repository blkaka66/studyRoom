package com.example.studyroom.repository;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    boolean existsByPhone(String phone);
    List<MemberEntity> findByShop(ShopEntity shop);
    MemberEntity findByNameAndPassword(String name, String password);

    MemberEntity findByPhoneAndPassword(String phone, String password);
    MemberEntity findByPhone(String phone);
    void deleteById(Long id);
    int countByShop(ShopEntity shop);
}

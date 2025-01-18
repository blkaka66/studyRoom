package com.example.studyroom.repository;

import com.example.studyroom.model.CouponEntity;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    CouponEntity findByCouponCodeAndShopId(String code, Long shopId);

    CouponEntity findById(long id);
}

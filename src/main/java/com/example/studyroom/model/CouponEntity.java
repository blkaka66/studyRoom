package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class CouponEntity extends BaseEntity  {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false, unique = true)
    private String couponCode;  // 쿠폰 코드

    @Column(nullable = false, unique = true)
    private String couponName;  // 할인 유형 (예: PERCENT, AMOUNT)

    @Column(nullable = false)
    private String discountType;  // 할인 유형 (예: PERCENT, AMOUNT)

    @Column(nullable = false)
    private Long discountAmount;  // 할인 금액 또는 비율 (예: 10%, 5000원)

}

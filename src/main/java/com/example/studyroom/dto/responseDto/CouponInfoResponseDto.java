package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CouponInfoResponseDto {
    private Long id;
    private String couponName;
    private String discountType;
    private BigDecimal discountAmount;
}

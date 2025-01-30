package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class CouponInfoResponseDto {
    private Long id;
    private String couponName;
    private String discountType;
    private Long discountAmount;
}

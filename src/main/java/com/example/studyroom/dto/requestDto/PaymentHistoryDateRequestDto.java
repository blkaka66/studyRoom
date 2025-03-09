package com.example.studyroom.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PaymentHistoryDateRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;

    // ShopPaymentRequestIncludeNameDto에서 startDate와 endDate를 추출하는 생성자
    public static PaymentHistoryDateRequestDto fromShopPaymentRequestIncludeNameDto(ShopPaymentRequestIncludeNameDto dto) {
        return PaymentHistoryDateRequestDto.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
    }
}

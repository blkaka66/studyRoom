package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Getter
@Setter
@Builder
public class PeriodTicketPaymentHistoryDto {
    private String ticketType;  // 기간권, 시간권
    private String name;             // 제품명 (예: 1주자유석, 30시간)
    private int amount;              // 가격 (예: 50000, 90000)
    private int days;    // 제품 기간 (예: 7D, 30H)
    private OffsetDateTime paymentDate;  // 결제일
    private String couponType;
    private BigDecimal couponAmount; //할인금액
}

package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
@Getter
@Setter
@Builder
public class TimeTicketPaymentHistoryDto {
    private String ticketType;  // 기간권, 시간권
    private String name;             // 제품명 (예: 1주자유석, 30시간)
    private int amount;              // 가격 (예: 50000, 90000)
    private int hours;    // 제품 기간 (예: 7D, 30H)
    private OffsetDateTime paymentDate;  // 결제일
}

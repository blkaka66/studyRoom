package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@Builder
public class PaymentHistoryDto {
    private List<TimeTicketPaymentHistoryDto> timeTicketPaymentHistoryDtoList;  // 여러 제품 결제정보를 담는 리스트
    private List<PeriodTicketPaymentHistoryDto> periodTicketPaymentHistoryDtoList;  // 여러 제품 결제정보를 담는 리스트
}

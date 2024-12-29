package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.PeriodTicketPaymentHistoryDto;
import com.example.studyroom.dto.responseDto.TimeTicketPaymentHistoryDto;
import com.example.studyroom.model.PeriodTicketEntity;

import java.util.List;

public interface PeriodTicketService extends BaseService<PeriodTicketEntity>{

    // 결제 처리 메서드 구현
    FinalResponseDto<String> processPayment(ShopPayRequestDto product , Long shopId, Long customerId);

    List<PeriodTicketPaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId);

    void removeExpiredTickets();

}

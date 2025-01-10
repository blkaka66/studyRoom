package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.PeriodTicketEntity;
import com.example.studyroom.model.RemainPeriodTicketEntity;

import java.util.List;
import java.util.Optional;

public interface PeriodTicketService extends BaseService<PeriodTicketEntity>{

    // 결제 처리 메서드 구현
    FinalResponseDto<String> processPayment(ShopPayRequestDto product , Long shopId, Long customerId);

    List<PeriodTicketPaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId);

    void removeExpiredTickets();

    RemainTicketInfoResponseDto getEndDate(Long shopId, Long customerId, String ticketCategory); //만료 안된 기간권 언제끝나는지

}

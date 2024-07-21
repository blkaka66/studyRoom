package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.TicketPaymentRequestDto;
import com.example.studyroom.model.TicketPaymentEntity;

public interface TicketPaymentService extends BaseService<TicketPaymentEntity> {


    // 결제 처리 메서드 구현
    TicketPaymentEntity processPayment(TicketPaymentRequestDto paymentRequestDto);
}

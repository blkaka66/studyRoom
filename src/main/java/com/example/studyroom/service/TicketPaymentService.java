package com.example.studyroom.service;

import com.example.studyroom.model.TicketEntity;
import com.example.studyroom.model.TicketHistoryEntity;

public interface TicketPaymentService extends BaseService<TicketHistoryEntity> {


    // 결제 처리 메서드 구현
    void processPayment(TicketHistoryEntity ticket);
}

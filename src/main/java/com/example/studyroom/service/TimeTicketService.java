package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.TimeTicketEntity;

public interface TimeTicketService extends BaseService<TimeTicketEntity>{

    // 결제 처리 메서드 구현
    FinalResponseDto<String> processPayment(ShopPayRequestDto product ,Long shopId, Long customerId);
}

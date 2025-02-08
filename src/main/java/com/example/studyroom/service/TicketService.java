package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.PaymentHistoryDateRequestDto;
import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.PaymentHistoryDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.function.BiConsumer;

public interface TicketService {

    <T extends TicketEntity> FinalResponseDto<String> processPayment(
            ShopPayRequestDto product, Long shopId, Long customerId,
            JpaRepository<T, Long> ticketRepository
           );

    FinalResponseDto<PaymentHistoryDto> getPaymentHistory(PaymentHistoryDateRequestDto requestDto,Long shopId, Long customerId);
}

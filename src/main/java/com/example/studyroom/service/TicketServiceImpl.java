package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.PaymentHistoryDto;
import com.example.studyroom.dto.responseDto.PeriodTicketPaymentHistoryDto;
import com.example.studyroom.dto.responseDto.TimeTicketPaymentHistoryDto;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.type.ApiResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
@Service
public class TicketServiceImpl extends BaseServiceImpl<TicketEntity> implements TicketService {
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final TimeTicketServiceImpl timeTicketService;
    private final PeriodTicketServiceImpl periodTicketService;

    public TicketServiceImpl(JpaRepository<TicketEntity, Long> repository , ShopRepository shopRepository,
                             MemberRepository memberRepository, TimeTicketServiceImpl timeTicketService,
                             PeriodTicketServiceImpl periodTicketService

                         ){
            super(repository);
            this.shopRepository = shopRepository;
            this.memberRepository = memberRepository;
            this.timeTicketService = timeTicketService;
            this.periodTicketService = periodTicketService;


    }

    @Override
    @Transactional
    public <T extends TicketEntity> FinalResponseDto<String> processPayment(
            ShopPayRequestDto product, Long shopId, Long customerId,
            JpaRepository<T, Long> ticketRepository
    ) {
        Optional<T> optionalTicket = ticketRepository.findById(product.getProductId());
        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        Optional<MemberEntity> optionalMember = memberRepository.findById(customerId);

        if (optionalTicket.isPresent() && optionalMember.isPresent() && optionalShop.isPresent()) {
            T ticket = optionalTicket.get();
            if (ticket instanceof TimeTicketEntity) {
                timeTicketService.processPayment(product,shopId,customerId);
            } else if (ticket instanceof PeriodTicketEntity) {
                periodTicketService.processPayment(product,shopId,customerId);
            }


        } else {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<PaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId){
        List<TimeTicketPaymentHistoryDto> timeTicketPaymentHistory = timeTicketService.getPaymentHistory(shopId,customerId);
        List<PeriodTicketPaymentHistoryDto> periodTicketPaymentHistory = periodTicketService.getPaymentHistory(shopId,customerId);
        if(timeTicketPaymentHistory.isEmpty() && periodTicketPaymentHistory.isEmpty()){
            //만약 결제기록이 단 하나도없으면
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        PaymentHistoryDto paymentHistoryListDto = PaymentHistoryDto.builder()
                .periodTicketPaymentHistoryDtoList(periodTicketPaymentHistory)
                .timeTicketPaymentHistoryDtoList(timeTicketPaymentHistory)
                .build();

        return FinalResponseDto.successWithData(paymentHistoryListDto);
    }
}

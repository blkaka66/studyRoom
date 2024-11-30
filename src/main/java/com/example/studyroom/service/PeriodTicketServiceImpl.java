package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
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

@Service
public class PeriodTicketServiceImpl extends BaseServiceImpl<PeriodTicketEntity> implements PeriodTicketService {
    private final PeriodTicketRepository periodTicketRepository;
    private final PeriodTicketHistoryRepository periodTicketHistoryRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final RemainPeriodTicketRepository remainPeriodTicketRepository;
    public PeriodTicketServiceImpl(JpaRepository<PeriodTicketEntity, Long> repository , PeriodTicketRepository periodTicketRepository,
                                   PeriodTicketHistoryRepository periodTicketHistoryRepository, ShopRepository shopRepository
                                    , MemberRepository memberRepository,
                                   RemainPeriodTicketRepository remainPeriodTicketRepository) {
        super(repository);
        this.periodTicketRepository = periodTicketRepository;
        this.periodTicketHistoryRepository = periodTicketHistoryRepository;
        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
    }

    @Transactional
    @Override
    public FinalResponseDto<String> processPayment(ShopPayRequestDto product, Long shopId, Long customerId) {
        Optional<PeriodTicketEntity> optionalTicket = periodTicketRepository.findById(product.getProductId());
        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        Optional<MemberEntity> optionalMember = memberRepository.findById(customerId);

        if(optionalTicket.isPresent() && optionalMember.isPresent() && optionalShop.isPresent()){
            PeriodTicketEntity ticket = optionalTicket.get();
            MemberEntity member = optionalMember.get();
            ShopEntity shop =  optionalShop.get();
            recordTicketHistory(shop, ticket,member);
            addEndDate(ticket , shopId, customerId,shop,member);

        }else{
            FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        return FinalResponseDto.success();
    }

    private void addEndDate (PeriodTicketEntity ticket, Long shopId , Long customerId , ShopEntity shop,MemberEntity member){ //기존 시간권이 있는경우 시간을더해줌
        Optional<RemainPeriodTicketEntity> optionalPeriodTicket= remainPeriodTicketRepository.findByShopIdAndMemberId(shopId,customerId);
        if(optionalPeriodTicket.isPresent()){//기존 기간권이 있다면
            RemainPeriodTicketEntity timeTicket = optionalPeriodTicket.get();
            timeTicket.setEndDate(timeTicket.getEndDate().plusDays(ticket.getDays()));
        }else{ //기존 기간권이없다면
            RemainPeriodTicketEntity periodTicket = new RemainPeriodTicketEntity();
            periodTicket.setMember(member);
            periodTicket.setShop(shop);
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            Duration period = Duration.ofDays(ticket.getDays());//현재시간 + 기간 ex3일)
            OffsetDateTime endDate = now.plus(period);
            periodTicket.setEndDate(endDate);
            remainPeriodTicketRepository.save(periodTicket);
        }
    }

    private void recordTicketHistory (ShopEntity shop, PeriodTicketEntity ticket, MemberEntity member){
        PeriodTicketHistoryEntity ticketHistory = new PeriodTicketHistoryEntity();
        ticketHistory.setMember(member);
        ticketHistory.setShop(shop);
        ticketHistory.setTicket(ticket);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ticketHistory.setPaymentDate(now);
        periodTicketHistoryRepository.save(ticketHistory);
    }

    @Override
    public List<PeriodTicketPaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId) {
        List<PeriodTicketHistoryEntity> periodTicketHistoryList = periodTicketHistoryRepository.findByShop_IdAndMember_Id(shopId,customerId);
        return periodTicketHistoryList.stream()
                .map(entity -> PeriodTicketPaymentHistoryDto.builder()
                        .ticketType(String.valueOf(entity.getTicket().getTicketType()))  // 기간권, 시간권 설정
                        .name(entity.getTicket().getName())       // 제품명 설정
                        .amount(entity.getTicket().getAmount())          // 가격 설정
                        .days(entity.getTicket().getDays()) // 제품 기간 설정
                        .paymentDate(entity.getPaymentDate())     // 결제일 설정
                        .build())
                .toList();

    }
}

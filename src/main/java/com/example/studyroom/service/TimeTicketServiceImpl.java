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
//시간권(ex)3시간)

@Service
public class TimeTicketServiceImpl extends BaseServiceImpl<TimeTicketEntity> implements TimeTicketService{
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final TimeTicketRepository timeTicketRepository;
    private final TimeTicketHistoryRepository timeTicketHistoryRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;
    private final CouponRepository couponRepository;
    public TimeTicketServiceImpl(JpaRepository<TimeTicketEntity, Long> repository,
                                 MemberRepository memberRepository
            ,  TimeTicketRepository timeTicketRepository,TimeTicketHistoryRepository timeTicketHistoryRepository
            , RemainTimeTicketRepository remainTimeTicketRepository, ShopRepository shopRepository, CouponRepository couponRepository) {
        super(repository);
        this.memberRepository = memberRepository;
        this.timeTicketRepository = timeTicketRepository;
        this.timeTicketHistoryRepository = timeTicketHistoryRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
        this.shopRepository = shopRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional
    @Override
    public FinalResponseDto<String> processPayment(ShopPayRequestDto product,Long shopId, Long customerId) {
        Optional<TimeTicketEntity> optionalTicket = timeTicketRepository.findById(product.getProductId());
        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        Optional<MemberEntity> optionalMember = memberRepository.findById(customerId);

        if(optionalTicket.isPresent() && optionalMember.isPresent() && optionalShop.isPresent()){
            TimeTicketEntity ticket = optionalTicket.get();
            MemberEntity member = optionalMember.get();
            ShopEntity shop =  optionalShop.get();
            recordTicketHistory(shop,ticket,member,product.getCouponId());
            addRemainTime(ticket , shopId, customerId,shop,member);

        }else{
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        return FinalResponseDto.success();
    }



    private void addRemainTime (TimeTicketEntity ticket, Long shopId , Long customerId, ShopEntity shop,MemberEntity member){ //기존 시간권이 있는경우 시간을더해줌
       Optional<RemainTimeTicketEntity> optionalTimeTicket= remainTimeTicketRepository.findByShopIdAndMemberId(shopId,customerId);

       if(optionalTimeTicket.isPresent()){
           RemainTimeTicketEntity timeTicket = optionalTimeTicket.get();
           timeTicket.setRemainTime(timeTicket.getRemainTime().plusHours(ticket.getHours()));
       }else{
           RemainTimeTicketEntity timeTicket = new RemainTimeTicketEntity();
           timeTicket.setMember(member);
           timeTicket.setShop(shop);
           System.out.println("타임티켓처음삼!!"+Duration.ofHours(ticket.getHours()));
           timeTicket.setRemainTime(Duration.ofHours(ticket.getHours()));
           remainTimeTicketRepository.save(timeTicket);
       }
    }

    private void recordTicketHistory (ShopEntity shop, TimeTicketEntity ticket,MemberEntity member ,Long couponId){
        TimeTicketHistoryEntity ticketHistory = new TimeTicketHistoryEntity();
        ticketHistory.setMember(member);
        ticketHistory.setShop(shop);
        ticketHistory.setTicket(ticket);
        if (couponId != null) {
            Optional<CouponEntity> coupon = couponRepository.findById(couponId);
            coupon.ifPresent(ticketHistory::setCoupon);  // Optional에서 값을 꺼내서 setCoupon 호출
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ticketHistory.setPaymentDate(now);
        timeTicketHistoryRepository.save(ticketHistory);
    }

    @Override
    public List<TimeTicketPaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId) {
        List<TimeTicketHistoryEntity> timeTicketHistoryList = timeTicketHistoryRepository.findByShop_IdAndMember_Id(shopId, customerId);

        return timeTicketHistoryList.stream()
                .map(entity -> TimeTicketPaymentHistoryDto.builder()
                        .ticketType(String.valueOf(TicketTypeEnum.TIME)) // 시간권, 기간권 설정
                        .name(entity.getTicket().getName()) // 제품명 설정
                        .amount(entity.getTicket().getAmount()) // 가격 설정
                        .hours(entity.getTicket().getHours()) // 제품 기간 설정
                        .paymentDate(entity.getPaymentDate()) // 결제일 설정
                        .couponType(entity.getCoupon() != null ? entity.getCoupon().getDiscountType() : null) // coupon이 null일 경우 null 할당
                        .couponAmount(entity.getCoupon() != null ? entity.getCoupon().getDiscountAmount() : null) // coupon이 null일 경우 null 할당
                        .build())
                .toList();
    }



}

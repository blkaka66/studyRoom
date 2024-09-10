package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.type.ApiResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
//시간권(ex)3시간)

@Service
public class TimeTicketServiceImpl extends BaseServiceImpl<TimeTicketEntity> implements TimeTicketService{
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final TimeTicketRepository timeTicketRepository;
    private final TimeTicketHistoryRepository timeTicketHistoryRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;
    public TimeTicketServiceImpl(JpaRepository<TimeTicketEntity, Long> repository,
                                 MemberRepository memberRepository
            ,  TimeTicketRepository timeTicketRepository,TimeTicketHistoryRepository timeTicketHistoryRepository
            , RemainTimeTicketRepository remainTimeTicketRepository, ShopRepository shopRepository) {
        super(repository);
        this.memberRepository = memberRepository;
        this.timeTicketRepository = timeTicketRepository;
        this.timeTicketHistoryRepository = timeTicketHistoryRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
        this.shopRepository = shopRepository;
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
            recordTicketHistory(shop, ticket,member);
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
           timeTicket.setRemainTime(timeTicket.getRemainTime().plus(ticket.getPeriod()));
       }else{
           RemainTimeTicketEntity timeTicket = new RemainTimeTicketEntity();
           timeTicket.setMember(member);
           timeTicket.setShop(shop);
           timeTicket.setRemainTime(ticket.getPeriod());
           remainTimeTicketRepository.save(timeTicket);
       }
    }

    private void recordTicketHistory (ShopEntity shop, TimeTicketEntity ticket,MemberEntity member){
        TimeTicketHistoryEntity ticketHistory = new TimeTicketHistoryEntity();
        ticketHistory.setMember(member);
        ticketHistory.setShop(shop);
        ticketHistory.setTicket(ticket);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ticketHistory.setPaymentDate(now);
        timeTicketHistoryRepository.save(ticketHistory);
    }


}

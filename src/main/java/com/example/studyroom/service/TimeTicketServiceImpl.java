package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.PaymentHistoryDateRequestDto;
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
public class TimeTicketServiceImpl extends BaseServiceImpl<TimeTicketEntity> implements TimeTicketService {
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final TimeTicketRepository timeTicketRepository;
    private final TimeTicketHistoryRepository timeTicketHistoryRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;
    private final CouponRepository couponRepository;
    private final TicketExpirationAlertRepository ticketExpirationAlertRepository;

    public TimeTicketServiceImpl(JpaRepository<TimeTicketEntity, Long> repository,
                                 MemberRepository memberRepository
            , TimeTicketRepository timeTicketRepository, TimeTicketHistoryRepository timeTicketHistoryRepository
            , RemainTimeTicketRepository remainTimeTicketRepository, ShopRepository shopRepository, CouponRepository couponRepository, TicketExpirationAlertRepository ticketExpirationAlertRepository) {
        super(repository);
        this.memberRepository = memberRepository;
        this.timeTicketRepository = timeTicketRepository;
        this.timeTicketHistoryRepository = timeTicketHistoryRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
        this.shopRepository = shopRepository;
        this.couponRepository = couponRepository;
        this.ticketExpirationAlertRepository = ticketExpirationAlertRepository;
    }

    @Transactional
    @Override
    public FinalResponseDto<String> processPayment(ShopPayRequestDto product, Long shopId, Long customerId) {
        Optional<TimeTicketEntity> optionalTicket = timeTicketRepository.findById(product.getProductId());
        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        Optional<MemberEntity> optionalMember = memberRepository.findById(customerId);

        if (optionalTicket.isPresent() && optionalMember.isPresent() && optionalShop.isPresent()) {
            TimeTicketEntity ticket = optionalTicket.get();
            MemberEntity member = optionalMember.get();
            ShopEntity shop = optionalShop.get();
            recordTicketHistory(shop, ticket, member, product.getCouponId());
            addOrUpdateRemainTicket(ticket, shopId, customerId, shop, member);
            recordTicketExpireDay(shop, ticket, member);
        } else {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        return FinalResponseDto.success();
    }

    private void addOrUpdateRemainTicket(TimeTicketEntity ticket, Long shopId, Long customerId, ShopEntity shop, MemberEntity member) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Optional<RemainTimeTicketEntity> optionalTicket =
                remainTimeTicketRepository.findByShopIdAndMemberIdAndExpiresAtAfter(shopId, customerId, now);

        Duration newRemainTime = Duration.ofHours(ticket.getHours());
        OffsetDateTime newExpiresAt = now.plusDays(ticket.getValidDays());

        if (optionalTicket.isPresent()) {
            RemainTimeTicketEntity existing = optionalTicket.get();

            // 남은 시간 누적
            existing.setRemainTime(existing.getRemainTime().plus(newRemainTime));

            // expiresAt 갱신
            OffsetDateTime existingExpires = existing.getExpiresAt();
            if (existingExpires.isAfter(now)) {
                existing.setExpiresAt(existingExpires.plusDays(ticket.getValidDays()));
            } else {
                existing.setExpiresAt(newExpiresAt);
            }

            remainTimeTicketRepository.save(existing);

        } else {
            RemainTimeTicketEntity newTicket = new RemainTimeTicketEntity();
            newTicket.setMember(member);
            newTicket.setShop(shop);
            newTicket.setRemainTime(newRemainTime);
            newTicket.setExpiresAt(newExpiresAt);
            remainTimeTicketRepository.save(newTicket);
        }
    }


//    private void addRemainTime(TimeTicketEntity ticket, Long shopId, Long customerId, ShopEntity shop, MemberEntity member) { //기존 시간권이 있는경우 시간을더해줌
//        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
//
//        Optional<RemainTimeTicketEntity> optionalTimeTicket = remainTimeTicketRepository.findByShopIdAndMemberIdAndExpiresAtBefore(shopId, customerId, now);
//
//        if (optionalTimeTicket.isPresent()) {
//            RemainTimeTicketEntity timeTicket = optionalTimeTicket.get();
//            timeTicket.setRemainTime(timeTicket.getRemainTime().plusHours(ticket.getHours()));
//        } else {
//            RemainTimeTicketEntity timeTicket = new RemainTimeTicketEntity();
//            timeTicket.setMember(member);
//            timeTicket.setShop(shop);
//            System.out.println("타임티켓처음삼!!" + Duration.ofHours(ticket.getHours()));
//            timeTicket.setRemainTime(Duration.ofHours(ticket.getHours()));
//            remainTimeTicketRepository.save(timeTicket);
//        }
//    }
//
//    private void addExpiresAt(TimeTicketEntity ticket, Long shopId, Long customerId) {
//        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
//
//        Optional<RemainTimeTicketEntity> optionalTimeTicket =
//                remainTimeTicketRepository.findByShopIdAndMemberIdAndExpiresAtBefore(shopId, customerId, now);
//
//        if (optionalTimeTicket.isEmpty()) {
//            return;
//        }
//
//        RemainTimeTicketEntity timeTicket = optionalTimeTicket.get();
//        OffsetDateTime existingExpiresAt = timeTicket.getExpiresAt();
//        OffsetDateTime newExpiresAt = now.plusDays(ticket.getValidDays());
//
//        if (existingExpiresAt.isAfter(now)) {
//            timeTicket.setExpiresAt(existingExpiresAt.plusDays(ticket.getValidDays()));
//        } else {
//            timeTicket.setExpiresAt(newExpiresAt);
//        }
//
//        remainTimeTicketRepository.save(timeTicket);
//    }


    private void recordTicketHistory(ShopEntity shop, TimeTicketEntity ticket, MemberEntity member, Long couponId) {
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

    private void recordTicketExpireDay(ShopEntity shop, TimeTicketEntity ticket, MemberEntity member) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Optional<TicketExpirationAlertEntity> alreadyExistAlert =
                ticketExpirationAlertRepository.findFirstByMemberIdAndShopIdAndTicketTypeAndSentFalse(
                        member.getId(), shop.getId(), "TIME"
                );

        if (alreadyExistAlert.isPresent()) {
            TicketExpirationAlertEntity existing = alreadyExistAlert.get();

            // 유효기간만큼 sendTime 갱신
            OffsetDateTime newSendTime = existing.getSendTime().plusDays(ticket.getValidDays());
            existing.setSendTime(newSendTime);

            ticketExpirationAlertRepository.save(existing); // 갱신 저장

        } else {
            // 새 알림 생성
            TicketExpirationAlertEntity ticketExpirationAlert = new TicketExpirationAlertEntity();
            ticketExpirationAlert.setMemberId(member.getId());
            ticketExpirationAlert.setShopId(shop.getId());
            ticketExpirationAlert.setTicketType("TIME");

            OffsetDateTime sendTime = now.plusDays(ticket.getValidDays());
            ticketExpirationAlert.setSendTime(sendTime);

            ticketExpirationAlert.setCreatedAt(now);
            ticketExpirationAlertRepository.save(ticketExpirationAlert);

        }
    }

    @Override
    public List<TimeTicketPaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        List<TimeTicketHistoryEntity> timeTicketHistoryList = timeTicketHistoryRepository.findByShop_IdAndMember_IdAndPaymentDateBetween(shopId, customerId, startDateTime, endDateTime);
        System.out.println("시간권기록" + timeTicketHistoryList.size());
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

package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ShopPayRequestDto;
import com.example.studyroom.dto.responseDto.*;

import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.type.ApiResult;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PeriodTicketServiceImpl extends BaseServiceImpl<PeriodTicketEntity> implements PeriodTicketService {
    private final PeriodTicketRepository periodTicketRepository;
    private final PeriodTicketHistoryRepository periodTicketHistoryRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final RemainPeriodTicketRepository remainPeriodTicketRepository;
    private final CouponRepository couponRepository;
    private final TicketExpirationAlertRepository ticketExpirationAlertRepository;

    public PeriodTicketServiceImpl(JpaRepository<PeriodTicketEntity, Long> repository, PeriodTicketRepository periodTicketRepository,
                                   PeriodTicketHistoryRepository periodTicketHistoryRepository,
                                   ShopRepository shopRepository
            , MemberRepository memberRepository,
                                   RemainPeriodTicketRepository remainPeriodTicketRepository, CouponRepository couponRepository,
                                   TicketExpirationAlertRepository ticketExpirationAlertRepository) {
        super(repository);
        this.periodTicketRepository = periodTicketRepository;
        this.periodTicketHistoryRepository = periodTicketHistoryRepository;
        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
        this.couponRepository = couponRepository;
        this.ticketExpirationAlertRepository = ticketExpirationAlertRepository;
    }

    @Transactional
    @Override
    public FinalResponseDto<String> processPayment(ShopPayRequestDto product, Long shopId, Long customerId) {
        Optional<PeriodTicketEntity> optionalTicket = periodTicketRepository.findById(product.getProductId());
        Optional<ShopEntity> optionalShop = shopRepository.findById(shopId);
        Optional<MemberEntity> optionalMember = memberRepository.findById(customerId);

        if (optionalTicket.isPresent() && optionalMember.isPresent() && optionalShop.isPresent()) {
            PeriodTicketEntity ticket = optionalTicket.get();
            MemberEntity member = optionalMember.get();
            ShopEntity shop = optionalShop.get();
            recordTicketHistory(shop, ticket, member, product.getCouponId());
            addOrUpdateRemainTicket(ticket, shopId, customerId, shop, member);

            recordTicketExpireDay(shop, ticket, member);
        } else {
            FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        return FinalResponseDto.success();
    }

    private void addOrUpdateRemainTicket(PeriodTicketEntity ticket, Long shopId, Long customerId, ShopEntity shop, MemberEntity member) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Optional<RemainPeriodTicketEntity> optionalTicket =
                remainPeriodTicketRepository.findByShopIdAndMemberIdAndEndDateAfterAndExpiresAtAfter(shopId, customerId, now, now);

        OffsetDateTime newEndDate = now.plusDays(ticket.getDays());
        OffsetDateTime newExpiresAt = now.plusDays(ticket.getValidDays());

        if (optionalTicket.isPresent()) {
            log.info("optionalTicket 남은거있음");
            RemainPeriodTicketEntity existing = optionalTicket.get();

            OffsetDateTime existingEnd = existing.getEndDate();
            OffsetDateTime existingExpires = existing.getExpiresAt();

            if (existingEnd.isAfter(now)) {
                existing.setEndDate(existingEnd.plusDays(ticket.getDays()));
            } else {
                existing.setEndDate(newEndDate);
            }

            if (existingExpires.isAfter(now)) {
                existing.setExpiresAt(existingExpires.plusDays(ticket.getValidDays()));
            } else {
                existing.setExpiresAt(newExpiresAt);
            }

            remainPeriodTicketRepository.save(existing);

        } else {
            log.info("optionalTicket 남은거없음");
            RemainPeriodTicketEntity newTicket = new RemainPeriodTicketEntity();
            newTicket.setMember(member);
            newTicket.setShop(shop);
            newTicket.setEndDate(newEndDate);
            newTicket.setExpiresAt(newExpiresAt);
            remainPeriodTicketRepository.save(newTicket);
        }
    }


    private void recordTicketHistory(ShopEntity shop, PeriodTicketEntity ticket, MemberEntity member, Long couponId) {
        PeriodTicketHistoryEntity ticketHistory = new PeriodTicketHistoryEntity();
        ticketHistory.setMember(member);
        ticketHistory.setShop(shop);
        ticketHistory.setTicket(ticket);
        if (couponId != null) {
            Optional<CouponEntity> coupon = couponRepository.findById(couponId);
            coupon.ifPresent(ticketHistory::setCoupon);  // Optional에서 값을 꺼내서 setCoupon 호출
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ticketHistory.setPaymentDate(now);
        periodTicketHistoryRepository.save(ticketHistory);
    }

    @Override
    public List<PeriodTicketPaymentHistoryDto> getPaymentHistory(Long shopId, Long customerId, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        List<PeriodTicketHistoryEntity> periodTicketHistoryList = periodTicketHistoryRepository.findByShop_IdAndMember_IdAndPaymentDateBetween(shopId, customerId, startDateTime, endDateTime);
        System.out.println("기간권권기록" + periodTicketHistoryList.size());
        return periodTicketHistoryList.stream()
                .map(entity -> PeriodTicketPaymentHistoryDto.builder()
//                        .ticketType(String.valueOf(entity.getTicket().getTicketType()))  // 기간권, 시간권 설정
                        .ticketType(String.valueOf(TicketTypeEnum.PERIOD))
                        .name(entity.getTicket().getName())       // 제품명 설정
                        .amount(entity.getTicket().getAmount())          // 가격 설정
                        .days(entity.getTicket().getDays()) // 제품 기간 설정
                        .paymentDate(entity.getPaymentDate())     // 결제일 설정
                        .couponType(entity.getCoupon() != null ? entity.getCoupon().getDiscountType() : null) // coupon이 null일 경우 null 할당
                        .couponAmount(entity.getCoupon() != null ? entity.getCoupon().getDiscountAmount() : null) // coupon이 null일 경우 null 할당
                        .build())
                .toList();

    }

    @Override //  remain periodrepository의 enddate가 현재시간보다 이전이면 그 row 삭제(만료된거니까)
    public void removeExpiredTickets() {
        OffsetDateTime now = OffsetDateTime.now(); // 현재 시간
        remainPeriodTicketRepository.deleteExpiredTickets(now);
    }

    private void recordTicketExpireDay(ShopEntity shop, PeriodTicketEntity ticket, MemberEntity member) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Optional<TicketExpirationAlertEntity> alreadyExistAlert =
                ticketExpirationAlertRepository.findFirstByMemberIdAndShopIdAndTicketTypeAndSentFalse(
                        member.getId(), shop.getId(), "PERIOD"
                );

        if (alreadyExistAlert.isPresent()) {
            TicketExpirationAlertEntity existing = alreadyExistAlert.get();

            // 유효기간만큼 sendTime 갱신
            OffsetDateTime newSendTime = existing.getSendTime().plusDays(ticket.getValidDays() - 1);
            existing.setSendTime(newSendTime);

            ticketExpirationAlertRepository.save(existing); // 갱신 저장

        } else {
            // 새 알림 생성
            TicketExpirationAlertEntity ticketExpirationAlert = new TicketExpirationAlertEntity();
            ticketExpirationAlert.setMemberId(member.getId());
            ticketExpirationAlert.setShopId(shop.getId());
            ticketExpirationAlert.setTicketType("PERIOD");

            OffsetDateTime sendTime = now.plusDays(ticket.getValidDays() - 1);
            ticketExpirationAlert.setSendTime(sendTime);

            ticketExpirationAlert.setCreatedAt(now);
            ticketExpirationAlertRepository.save(ticketExpirationAlert);

        }
    }

    @Override
    public RemainTicketInfoResponseDto getEndDate(Long shopId, Long customerId, String ticketCategory) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Optional<RemainPeriodTicketEntity> RemainPeriodTicketEntity = remainPeriodTicketRepository.findByShopIdAndMemberIdAndEndDateAfterAndExpiresAtAfter(shopId, customerId, now, now);
        return RemainPeriodTicketEntity.map(remainPeriodTicketEntity -> RemainTicketInfoResponseDto.builder()
                .seatType(ticketCategory)
                .endDate(remainPeriodTicketEntity.getEndDate())
                .build()).orElse(null);
    }
}

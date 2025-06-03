package com.example.studyroom.service;

import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.MessageListener;
import jakarta.transaction.Transactional;

@Service
public class RedisExpirationListener implements MessageListener {
    private final RemainPeriodTicketRepository remainPeriodTicketRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;
    private final RemainTimeTicketService remainTimeTicketService;
    private final SeatRepository seatRepository;
    private final MemberService memberService;
    private final EnterHistoryRepository enterHistoryRepository;
    private final SeatService seatService;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    @Autowired
    public RedisExpirationListener(RemainPeriodTicketRepository remainPeriodTicketRepository
            , RemainTimeTicketRepository remainTimeTicketRepository,
                                   RemainTimeTicketService remainTimeTicketService,
                                   SeatRepository seatRepository, MemberService memberService
            , EnterHistoryRepository enterHistoryRepository, SeatService seatService,
                                   MemberRepository memberRepository, ShopRepository shopRepository) {
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
        this.remainTimeTicketService = remainTimeTicketService;
        this.seatRepository = seatRepository;
        this.memberService = memberService;
        this.enterHistoryRepository = enterHistoryRepository;
        this.seatService = seatService;
        this.memberRepository = memberRepository;
        this.shopRepository = shopRepository;
    }

    @Transactional
    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("TTL 이벤트 시작");

        String expiredKey = message.toString();

        System.out.println("expiredKey" + expiredKey);

        // Key에서 필요한 정보 추출
        Long seatId = extractSeatIdFromKey(expiredKey);
        Long userId = extractUserIdFromKey(expiredKey);
        Long shopId = extractShopIdFromKey(expiredKey);
        System.out.println("seatId" + seatId);
        System.out.println("userId" + userId);
        System.out.println("shopId" + shopId);
//        SeatEntity seat = seatRepository.findById(seatId)
//                .orElseThrow(() -> new IllegalArgumentException("Seat not found for id: " + seatId));
        MemberEntity member = memberRepository.findById(userId).orElse(null);
        if (member == null) {
            return;
        }
        ShopEntity shop = shopRepository.findById(shopId).orElse(null);
        if (shop == null) {
            return;
        }
        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByMember(member);
        boolean isUpdateSeatAvailability = seatService.updateSeatAvailability(enterHistory.getSeat().getId());
        if (!isUpdateSeatAvailability) {
            System.out.println("존재하지 않는 좌석: " + expiredKey);
        }
        if (expiredKey.startsWith("periodSeat:")) {
            remainPeriodTicketRepository.deleteByShopAndMember(shop, member);
            memberService.updateExitTime(enterHistory);
            System.out.println("기간권 만료 처리 완료: userId = " + userId);
        } else if (expiredKey.startsWith("timeSeat:")) {
            remainTimeTicketRepository.deleteByShopAndMember(shop, member);
            memberService.updateExitTime(enterHistory);
            System.out.println("시간권 만료 처리 완료: userId = " + userId);
        } else {
            throw new IllegalArgumentException("존재하지 않는 티켓 종류: " + expiredKey);
        }


        // 좌석 상태를 사용 가능으로 변경
//        seat.setAvailable(true);
//        seatRepository.save(seat);
//
//        System.out.println("좌석 사용 가능 상태로 변경: seatId = " + seatId);
    }


    private Long extractSeatIdFromKey(String key) {
        String[] parts = key.split(":");
        return Long.valueOf(parts[1]);
    }

    private Long extractUserIdFromKey(String key) {
        String[] parts = key.split(":");
        return Long.valueOf(parts[3]);
    }

    private Long extractShopIdFromKey(String key) {
        String[] parts = key.split(":");
        return Long.valueOf(parts[5]);
    }

}

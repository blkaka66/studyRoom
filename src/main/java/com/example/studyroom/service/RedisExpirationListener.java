package com.example.studyroom.service;
import com.example.studyroom.model.EnterHistoryEntity;
import com.example.studyroom.model.RemainPeriodTicketEntity;
import com.example.studyroom.model.RemainTimeTicketEntity;
import com.example.studyroom.model.SeatEntity;
import com.example.studyroom.repository.EnterHistoryRepository;
import com.example.studyroom.repository.RemainPeriodTicketRepository;
import com.example.studyroom.repository.RemainTimeTicketRepository;
import com.example.studyroom.repository.SeatRepository;
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
    @Autowired
    public RedisExpirationListener(RemainPeriodTicketRepository remainPeriodTicketRepository
    , RemainTimeTicketRepository remainTimeTicketRepository,
    RemainTimeTicketService remainTimeTicketService,
    SeatRepository seatRepository , MemberService memberService
    , EnterHistoryRepository enterHistoryRepository , SeatService seatService) {
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
        this.remainTimeTicketService = remainTimeTicketService;
        this.seatRepository = seatRepository;
        this.memberService = memberService;
        this.enterHistoryRepository = enterHistoryRepository;
        this.seatService = seatService;
    }

    @Transactional
    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("TTL 이벤트 시작");

        String expiredKey = message.toString();

        System.out.println("expiredKey"+expiredKey);

        // Key에서 필요한 정보 추출
        Long seatId = extractSeatIdFromKey(expiredKey);
        Long userId = extractUserIdFromKey(expiredKey);
        Long shopId = extractShopIdFromKey(expiredKey);
        System.out.println("seatId"+seatId);
        System.out.println("userId"+userId);
        System.out.println("shopId"+shopId);
//        SeatEntity seat = seatRepository.findById(seatId)
//                .orElseThrow(() -> new IllegalArgumentException("Seat not found for id: " + seatId));

        EnterHistoryEntity enterHistory = enterHistoryRepository.findActiveByCustomerId(userId);
        boolean isUpdateSeatAvailability= seatService.updateSeatAvailability(enterHistory.getSeat().getId());
        if(!isUpdateSeatAvailability){
            System.out.println("존재하지 않는 좌석: " + expiredKey);
        }
        if (expiredKey.startsWith("periodSeat:")) {
            remainPeriodTicketRepository.deleteByShopIdAndMemberId(shopId, userId);
            memberService.updateExitTime(enterHistory);
            System.out.println("기간권 만료 처리 완료: userId = " + userId);
        } else if (expiredKey.startsWith("timeSeat:")) {
            remainTimeTicketRepository.deleteByShopIdAndMemberId(shopId, userId);
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

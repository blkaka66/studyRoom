package com.example.studyroom.service;
import com.example.studyroom.model.RemainPeriodTicketEntity;
import com.example.studyroom.model.RemainTimeTicketEntity;
import com.example.studyroom.model.SeatEntity;
import com.example.studyroom.repository.RemainPeriodTicketRepository;
import com.example.studyroom.repository.RemainTimeTicketRepository;
import com.example.studyroom.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.MessageListener;

@Service
public class RedisExpirationListener implements MessageListener {
    private final RemainPeriodTicketRepository remainPeriodTicketRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;
    private final RemainTimeTicketService remainTimeTicketService;
    private final SeatRepository seatRepository;

    @Autowired
    public RedisExpirationListener(RemainPeriodTicketRepository remainPeriodTicketRepository
    , RemainTimeTicketRepository remainTimeTicketRepository, RemainTimeTicketService remainTimeTicketService, SeatRepository seatRepository) {
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;
        this.remainTimeTicketService = remainTimeTicketService;
        this.seatRepository = seatRepository;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("TTL 이벤트 시작");
        String expiredKey = message.toString();

        // Key에서 필요한 정보 추출
        Long seatId = extractSeatIdFromKey(expiredKey);
        Long userId = extractUserIdFromKey(expiredKey);
        Long shopId = extractShopIdFromKey(expiredKey);


        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found for id: " + seatId));

        if (expiredKey.startsWith("periodSeat:")) {
            remainPeriodTicketRepository.deleteByShopIdAndMemberId(shopId, userId);
            System.out.println("기간권 만료 처리 완료: userId = " + userId);
        } else if (expiredKey.startsWith("timeSeat:")) {
            remainTimeTicketRepository.deleteByShopIdAndMemberId(shopId, userId);
            System.out.println("시간권 만료 처리 완료: userId = " + userId);
        } else {
            throw new IllegalArgumentException("존재하지 않는 티켓 종류: " + expiredKey);
        }

        // 좌석 상태를 사용 가능으로 변경
        seat.setAvailable(true);
        seatRepository.save(seat);

        System.out.println("좌석 사용 가능 상태로 변경: seatId = " + seatId);
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

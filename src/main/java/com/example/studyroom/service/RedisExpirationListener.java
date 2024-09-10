package com.example.studyroom.service;
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
    public void onMessage(Message message, byte[] pattern) { //ttl만료이벤트가 발생하면 호출됨
        String expiredKey = message.toString();
        if (expiredKey.startsWith("periodSeat:")) {//만약 ttl이 만료됐으면
            Long seatId = extractSeatIdFromKey(expiredKey);
            Long userId = extractUserIdFromKey(expiredKey);
            remainPeriodTicketRepository.deleteByShopIdAndMemberId(seatId, userId);
        }else if(expiredKey.startsWith("timeSeat:")){
            Long seatId = extractSeatIdFromKey(expiredKey);
            Long userId = extractUserIdFromKey(expiredKey);
            Long shopId = extractShopIdFromKey(expiredKey);
//            remainTimeTicketRepository.deleteByShopIdAndMemberId(seatId, userId);
            SeatEntity seat = seatRepository.findById(seatId).orElseThrow();
            RemainTimeTicketEntity remainTimeTicketEntity = remainTimeTicketRepository.findByShopIdAndMemberId(shopId, userId).orElseThrow();
            seat.setAvailable(true);
            seatRepository.save(seat);
            remainTimeTicketService.delete(remainTimeTicketEntity);
        }
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

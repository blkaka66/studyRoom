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
    public void onMessage(Message message, byte[] pattern) { //ttl만료이벤트가 발생하면 호출됨
        String expiredKey = message.toString();
        Long seatId = extractSeatIdFromKey(expiredKey);
        Long userId = extractUserIdFromKey(expiredKey);
        Long shopId = extractShopIdFromKey(expiredKey);
        SeatEntity seat = seatRepository.findById(seatId).orElseThrow();
        if (expiredKey.startsWith("periodSeat:")) {//만약 ttl이 만료됐으면
            remainPeriodTicketRepository.deleteByShopIdAndMemberId(shopId, userId);
            RemainPeriodTicketEntity remainPeriodTicket = remainPeriodTicketRepository.findByShopIdAndMemberId(shopId, userId).orElseThrow();
            remainPeriodTicketRepository.delete(remainPeriodTicket);
        }else if(expiredKey.startsWith("timeSeat:")){
            remainTimeTicketRepository.deleteByShopIdAndMemberId(shopId, userId);
            RemainTimeTicketEntity remainTimeTicketEntity = remainTimeTicketRepository.findByShopIdAndMemberId(shopId, userId).orElseThrow();
            remainTimeTicketRepository.delete(remainTimeTicketEntity);
        }else{
            throw new IllegalArgumentException("존재하지 않는 티켓종류(redis ttl 만료시 이벤트): " + expiredKey);
        }
        seat.setAvailable(true);
        seatRepository.save(seat);

        
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

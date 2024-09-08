package com.example.studyroom.service;
import com.example.studyroom.repository.RemainPeriodTicketRepository;
import com.example.studyroom.repository.RemainTimeTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.MessageListener;

@Service
public class RedisExpirationListener implements MessageListener {
    private final RemainPeriodTicketRepository remainPeriodTicketRepository;
    private final RemainTimeTicketRepository remainTimeTicketRepository;
    @Autowired
    public RedisExpirationListener(RemainPeriodTicketRepository remainPeriodTicketRepository
    , RemainTimeTicketRepository remainTimeTicketRepository) {
        this.remainPeriodTicketRepository = remainPeriodTicketRepository;
        this.remainTimeTicketRepository = remainTimeTicketRepository;

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
            remainTimeTicketRepository.deleteByShopIdAndMemberId(seatId, userId);
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

}

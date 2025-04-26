package com.example.studyroom.scheduler;

import com.example.studyroom.kafka.producer.SeatAlertProducer;
import com.example.studyroom.model.SeatExpirationAlertEntity;
import com.example.studyroom.repository.SeatExpirationAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class SeatExpirationAlertScheduler {

    private final SeatExpirationAlertRepository alertRepository;
    private final SeatAlertProducer seatAlertProducer;
    private static final Logger log = LoggerFactory.getLogger(SeatExpirationAlertScheduler.class);

    public SeatExpirationAlertScheduler(SeatExpirationAlertRepository alertRepository,
                                        SeatAlertProducer seatAlertProducer) {
        this.alertRepository = alertRepository;
        this.seatAlertProducer = seatAlertProducer;
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void processExpiringSeats() {
        OffsetDateTime now = OffsetDateTime.now();
        List<SeatExpirationAlertEntity> alerts = alertRepository.findBySendTimeBeforeAndSentFalse(now);

        for (SeatExpirationAlertEntity alert : alerts) {
            try {
                seatAlertProducer.sendSeatExpirationWarning(
                        alert.getMemberId(),
                        alert.getSeatId(),
                        alert.getShopId(),
                        alert.getSendTime(),
                        alert.getTicketType()
                );
                alert.setSent(true);
                alertRepository.save(alert); // 중복 방지 처리
            } catch (Exception e) {
                log.error("좌석 만료 알림 처리 실패 - id: {}", alert.getId(), e);
            }
        }
    }
}

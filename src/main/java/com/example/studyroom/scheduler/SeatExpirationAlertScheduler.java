package com.example.studyroom.scheduler;

import com.example.studyroom.kafka.producer.SeatAlertProducer;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.SeatExpirationAlertEntity;
import com.example.studyroom.model.notice.NoticeType;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.SeatExpirationAlertRepository;
import com.example.studyroom.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SeatExpirationAlertScheduler {

    private final SeatExpirationAlertRepository alertRepository;
    private final SeatAlertProducer seatAlertProducer;

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public SeatExpirationAlertScheduler(SeatExpirationAlertRepository alertRepository,
                                        SeatAlertProducer seatAlertProducer, MemberService memberService
            , MemberRepository memberRepository) {
        this.alertRepository = alertRepository;
        this.seatAlertProducer = seatAlertProducer;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    //  @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void processExpiringSeats() {
        OffsetDateTime now = OffsetDateTime.now();
        List<SeatExpirationAlertEntity> alerts = alertRepository.findBySendTimeBeforeAndSentFalse(now);
        log.info("Found {} seats to send", alerts.size());
        for (SeatExpirationAlertEntity alert : alerts) {
            try {
                Optional<MemberEntity> member = memberRepository.findById(alert.getMemberId());
                if (member.isEmpty()) {
                    return;
                }
                MemberEntity memberEntity = member.get();
                memberService.saveMemberNotice(memberEntity, "이용 종료 알림", "자리 이용 시간이 10분 남았습니다.",
                        NoticeType.EXPIRED, now);
            } catch (Exception e) {
                log.error("좌석 만료 DB 저장 실패 - id: {}", alert.getId(), e);
            }

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

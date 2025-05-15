package com.example.studyroom.scheduler;

import com.example.studyroom.kafka.producer.TicketAlertProducer;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.TicketExpirationAlertEntity;
import com.example.studyroom.model.notice.NoticeType;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.TicketExpirationAlertRepository;
import com.example.studyroom.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TicketExpirationAlertScheduler {

    private final TicketExpirationAlertRepository ticketExpirationAlertRepository;
    private final TicketAlertProducer ticketAlertProducer;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public TicketExpirationAlertScheduler(TicketExpirationAlertRepository ticketExpirationAlertRepository,
                                          TicketAlertProducer ticketAlertProducer, MemberRepository memberRepository
            , MemberService memberService) {
        this.ticketExpirationAlertRepository = ticketExpirationAlertRepository;
        this.ticketAlertProducer = ticketAlertProducer;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    @Scheduled(fixedRate = 1000) // 1분마다 실행
    public void processTicketExpirationAlerts() {
        OffsetDateTime now = OffsetDateTime.now();
        List<TicketExpirationAlertEntity> alerts = ticketExpirationAlertRepository.findBySendTimeBeforeAndSentFalse(now);
        log.info("티켓만료 하루전 갯수{} ", alerts.size());

        for (TicketExpirationAlertEntity alert : alerts) {
            try {
                Optional<MemberEntity> member = memberRepository.findById(alert.getMemberId());
                if (member.isEmpty()) {
                    return;
                }
                MemberEntity memberEntity = member.get();
                memberService.saveMemberNotice(memberEntity, "티켓 만료 알림", "티켓 만료 하루 전입니다.",
                        NoticeType.EXPIRED, now);
            } catch (Exception e) {
                log.error("티켓 만료 DB 저장 실패 - id: {}", alert.getId(), e);
            }

            try {
                ticketAlertProducer.sendTicketExpirationWarning(
                        alert.getMemberId(),
                        alert.getShopId(),
                        alert.getSendTime(),
                        alert.getTicketType()
                );
                alert.setSent(true);
                ticketExpirationAlertRepository.save(alert); // 중복 방지
            } catch (Exception e) {
                log.error("티켓 만료 알림 전송 실패 - id: {}", alert.getId(), e);
            }
        }
    }
}

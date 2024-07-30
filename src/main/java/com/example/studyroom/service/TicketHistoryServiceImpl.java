package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.TicketHistoryRequestDto;
import com.example.studyroom.model.TicketEntity;
import com.example.studyroom.model.TicketHistoryEntity;
import com.example.studyroom.repository.TicketHistoryRepository;
import com.example.studyroom.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class TicketHistoryServiceImpl extends BaseServiceImpl <TicketHistoryEntity> implements TicketHistoryService {

    private final TicketHistoryRepository repository;
    private final TicketRepository ticketRepository;

    public TicketHistoryServiceImpl(TicketHistoryRepository repository, TicketRepository ticketRepository) {
        super(repository);
        this.repository = repository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public TicketHistoryEntity processPayment(TicketHistoryRequestDto paymentRequestDto) {
        // member
        // ㄴ 토큰이용
        // Optional<MemberEntity> member =
        // ticket
//        Optional<TicketEntity> ticket = ticketRepository.findById(paymentRequestDto.getProductId());
//        if(ticket.isEmpty()) {
//            return null;
//        }
        TicketEntity ticket = ticketRepository.findById(paymentRequestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 상품입니다."));
        // ㄴ DTO
        // startDate
        // endDate
        // expired = false

        TicketHistoryEntity ticketPayment = new TicketHistoryEntity();
//        ticketPayment.setTicket(ticket.get());
        ticketPayment.setTicket(ticket);
        //ticketPayment.setMember(member.get());
        ticketPayment.setExpired(false);
        setStart(ticketPayment);

        this.create(ticketPayment);

        return ticketPayment;
    }

    //    @Override
    private void setStart(TicketHistoryEntity ticket) {
        // 결제 시점 설정
        ticket.setStartDate(OffsetDateTime.now());//결제는 호출된 당시시간을 저장
        // 종료 시점 설정
        ticket.setEndDate(calculateEndDate(ticket)); //현재시간+period 근데 여기선 ticketentity가필요한데 어쩌지?
    }



    // 종료 시점을 계산하는 메서드
    private OffsetDateTime calculateEndDate(TicketHistoryEntity ticketPayment) {
        int period = ticketPayment.getTicket().getPeriod();
        if ("기간권".equals(ticketPayment.getTicket().getType())) {
            return ticketPayment.getStartDate().plusDays(period);//기간권이면 plusDays라는 내장함수를 이용
        } else if ("시간권".equals(ticketPayment.getTicket().getType())) {
            Duration newRemainTime = ticketPayment.getRemainTime() != null
                    ? ticketPayment.getRemainTime().plusHours(period) // 기존 remainTime이 있으면(기존 시간권에서 연장을한다면) period만큼 더하기
                    : Duration.ofHours(period); // remainTime이 없으면(새로 시간권을 끊는경우라면) 새로 Duration 덮어쓰기

            // remainTime 업데이트
            ticketPayment.setRemainTime(newRemainTime);
            return null; //시간권은 remaintime만 업데이트 하고 enddate는 기간권에서만 필요한 필드라 null을 리턴
        } else {
            throw new IllegalArgumentException("Unknown type: " + ticketPayment.getTicket().getType()); //예외처리
        }
    }


}

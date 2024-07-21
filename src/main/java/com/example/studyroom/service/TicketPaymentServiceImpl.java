package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.TicketPaymentRequestDto;
import com.example.studyroom.model.TicketEntity;
import com.example.studyroom.model.TicketPaymentEntity;
import com.example.studyroom.repository.TicketPaymentRepository;
import com.example.studyroom.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class TicketPaymentServiceImpl extends BaseServiceImpl <TicketPaymentEntity> implements TicketPaymentService {

    private final TicketPaymentRepository repository;
    private final TicketRepository ticketRepository;

    public TicketPaymentServiceImpl(TicketPaymentRepository repository, TicketRepository ticketRepository) {
        super(repository);
        this.repository = repository;
        this.ticketRepository = ticketRepository;
    }


    //    @Override
    public void setStart(TicketPaymentEntity ticket) {
        // 결제 시점 설정
        ticket.setStartDate(OffsetDateTime.now());//결제는 호출된 당시시간을 저장
        // 종료 시점 설정
        ticket.setEndDate(calculateEndDate(ticket)); //현재시간+period 근데 여기선 ticketentity가필요한데 어쩌지?
    }



    // 종료 시점을 계산하는 메서드
    private OffsetDateTime calculateEndDate(TicketPaymentEntity ticketPayment) {
        int period = ticketPayment.getTicket().getPeriod();
        if ("기간권".equals(ticketPayment.getTicket().getType())) {
            return ticketPayment.getStartDate().plusDays(period);//기간권이면 plusDays라는 내장함수를 이용
        } else if ("시간권".equals(ticketPayment.getTicket().getType())) {
            return ticketPayment.getStartDate().plusHours(period);//시간권이면 plusHours라는 내장함수를 이용
        } else {
            throw new IllegalArgumentException("Unknown type: " + ticketPayment.getTicket().getType()); //예외처리
        }
    }


    @Override
    public TicketPaymentEntity processPayment(TicketPaymentRequestDto paymentRequestDto) {
        // member
        // ㄴ 토큰이용
        // Optional<MemberEntity> member =
        // ticket
        Optional<TicketEntity> ticket = ticketRepository.findById(paymentRequestDto.getProductId());
        if(ticket.isEmpty()) {
            return null;
        }
        // ㄴ DTO
        // startDate
        // endDate
        // expired = false

        TicketPaymentEntity ticketPayment = new TicketPaymentEntity();
        ticketPayment.setTicket(ticket.get());
        //ticketPayment.setMember(member.get());
        ticketPayment.setExpired(false);
        setStart(ticketPayment);

        this.create(ticketPayment);

        return ticketPayment;
    }
}

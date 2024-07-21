package com.example.studyroom.service;

import com.example.studyroom.model.TicketEntity;
import com.example.studyroom.model.TicketHistoryEntity;

import java.time.OffsetDateTime;

public class TicketPaymentServiceImpl extends BaseServiceImpl <TicketHistoryEntity> implements TicketPaymentService {


    @Override
    public void setStart(TicketHistoryEntity ticket) {
        // 결제 시점 설정
        ticket.setStartDate(OffsetDateTime.now());//결제는 호출된 당시시간을 저장

        // 종료 시점 설정
        ticket.setEndDate(calculateEndDate(ticket)); //현재시간+period 근데 여기선 ticketentity가필요한데 어쩌지?


    }



    // 종료 시점을 계산하는 메서드
    private OffsetDateTime calculateEndDate(TicketEntity ticket) {
        int period = ticket.getPeriod();
        if ("기간권".equals(ticket.getType())) {
            return ticket.getStartDate().plusDays(period);//기간권이면 plusDays라는 내장함수를 이용
        } else if ("시간권".equals(ticket.getType())) {
            return ticket.getStartDate().plusHours(period);//시간권이면 plusHours라는 내장함수를 이용
        } else {
            throw new IllegalArgumentException("Unknown type: " + ticket.getType()); //예외처리
        }
    }


}

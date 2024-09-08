//package com.example.studyroom.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "memberActiveTicket")
//
//public class MemberActiveTicketEntity extends BaseEntity {
//    //Todo:로그인하면 결제한티켓정보확인 1순위) 기간권 2순위) 시간권
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
//    private MemberEntity member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ticketHistoryId", foreignKey = @ForeignKey(name = "fk_ticketHistory_id"))
//    private TicketHistoryEntity ticketHistory;
//
//}

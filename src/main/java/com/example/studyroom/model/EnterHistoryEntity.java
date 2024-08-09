package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Setter;

import java.time.OffsetDateTime;

public class EnterHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY) //lazy가 있어야 EnterHistroyEntity를 조회할떄 MemberEntity를 조회
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @JoinColumn(name = "seatId", foreignKey = @ForeignKey(name = "fk_seat_id"))
    private SeatEntity seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketHistoryId", foreignKey = @ForeignKey(name = "fk_ticket_history_id"))
    private TicketHistoryEntity ticketHistoryEntity;

    @Column(nullable = false)
    private OffsetDateTime enterTime;

    @Column(nullable = false)
    private OffsetDateTime expiredTime;

    @Setter
    @Column(nullable = false)
    private OffsetDateTime closeTime;

    public EnterHistoryEntity(Long member, SeatEntity seat, TicketHistoryEntity ticketHistory, OffsetDateTime now, OffsetDateTime expiredTime) {
    }

    public Long getSeatId() {
        return seat.getId();
    }

}

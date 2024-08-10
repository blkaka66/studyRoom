package com.example.studyroom.model;

import com.example.studyroom.common.DurationConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "ticketPayment")

public class TicketHistoryEntity extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketId", foreignKey = @ForeignKey(name = "fk_ticket_id"))
    private TicketEntity ticket;



    @Column(nullable = false)
    private OffsetDateTime startDate;

    @Column(nullable = false)
    private OffsetDateTime endDate;

    @Convert(converter = DurationConverter.class)
    @Column(nullable = true)
    private Duration remainTime;

    @Column(nullable = false)
    private Boolean isActive = false;

    @Column(nullable = false)
    private Boolean expired = false;

    // TODO: 얼마?

    // A 기간권 첫번째 구입
    // -> 2024.07.10 00:00:00 ~ 2024.08.09 23:59:59
    // A 기간권 두번째 구입 --- 2024.08.07 22:30:00
    // -> 2024.08.10 00:00:00 ~
}

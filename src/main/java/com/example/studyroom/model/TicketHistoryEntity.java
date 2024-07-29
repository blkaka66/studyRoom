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
    @JoinColumn(name = "tickeId", foreignKey = @ForeignKey(name = "fk_ticket_id"))
    private TicketEntity ticket;

    @Column(nullable = false)
    private OffsetDateTime startDate;

    @Column(nullable = false)
    private OffsetDateTime endDate;

    @Convert(converter = DurationConverter.class)
    @Column(nullable = true)
    private Duration remainTime;

    @Column(nullable = false)
    private Boolean expired = false;
}

package com.example.studyroom.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
//기간권 티켓을 산 기록
@Getter
@Setter
@Entity
@Table(name = "periodTicketHistory")
public class PeriodTicketHistoryEntity extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId", foreignKey = @ForeignKey(name = "fk_shop_id"))
    private ShopEntity shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketId", foreignKey = @ForeignKey(name = "fk_ticket_id"))
    private PeriodTicketEntity ticket;

    @Column(nullable = false)
    private OffsetDateTime paymentDate;//결제일
}

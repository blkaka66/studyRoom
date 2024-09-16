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
@Table(name = "remainTimeTicket")
public class RemainTimeTicketEntity extends RemainTicketEntity{
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
//    private MemberEntity member;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "shopId", foreignKey = @ForeignKey(name = "fk_shop_id"))
//    private ShopEntity shop;

    @Convert(converter = DurationConverter.class)
    @Column(nullable = false)
    private Duration remainTime;
}

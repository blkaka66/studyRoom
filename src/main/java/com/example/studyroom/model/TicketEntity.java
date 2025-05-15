package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass

public class TicketEntity extends BaseEntity {
    //referencedColumnName(참조할 외래 column명) 가없으면 코드가 알아서 id를 참조한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int validDays;
}

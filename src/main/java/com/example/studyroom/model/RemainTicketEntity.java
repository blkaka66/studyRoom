package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class RemainTicketEntity extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopId", foreignKey = @ForeignKey(name = "fk_shop_id"))
    private ShopEntity shop;
}

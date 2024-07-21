package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room")
public class RoomEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean onService;
}

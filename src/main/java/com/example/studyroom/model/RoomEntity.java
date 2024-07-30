package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "room")
    private List<SeatEntity> seats = new ArrayList<>();
}

//
//SELECT * FROM ROOM where shop_id = 10;
//List<RoomEntity>
//
//
//room.getSeats()
//
//
//
//SELECT * FROM SEAT WEHRE room_id = 51;
package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seat")

public class SeatEntity extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roomId", foreignKey = @ForeignKey(name = "roomId"))
    private RoomEntity room;

    @Column(nullable = false, unique = true)
    private String seatCode;

    @Column(nullable = false)
    private Boolean onService;
}

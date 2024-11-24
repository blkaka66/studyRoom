package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seat", uniqueConstraints = {
        @UniqueConstraint( name="uk-seat-room", columnNames={"seat_code", "room_id"} )
})
public class SeatEntity extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "roomId"))
    private RoomEntity room;

    @Column(nullable = false, name="seat_code")
    private int seatCode;

    @Column(nullable = false)
    private Boolean onService;

    @Column(nullable = true)
    private Boolean available;
}

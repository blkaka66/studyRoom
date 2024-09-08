package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "enterHistory")
public class EnterHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY) //lazy가 있어야 EnterHistroyEntity를 조회할떄 MemberEntity를 조회
    @JoinColumn(name = "customerId", foreignKey = @ForeignKey(name = "fk_customer_id"))
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @Getter
    @JoinColumn(name = "seatId", foreignKey = @ForeignKey(name = "fk_seat_id"))
    private SeatEntity seat;

    @Column(nullable = false)
    private OffsetDateTime enterTime;

    @Setter
    @Column(nullable = false)
    private OffsetDateTime exitTime;

    public EnterHistoryEntity() {

    }

    public EnterHistoryEntity(Long memberId, SeatEntity seat, OffsetDateTime now, OffsetDateTime exitTime) {
        super();
    }


    public Long getSeatId() {
        return seat.getId();
    }

}

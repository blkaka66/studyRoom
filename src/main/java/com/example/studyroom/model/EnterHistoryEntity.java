package com.example.studyroom.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "enterHistory")
public class EnterHistoryEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customerId",
            foreignKey = @ForeignKey(name = "fk_customer_id"),
            nullable = true // 외래 키를 null로 설정 가능
    )
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "FK_SHOP_ID"))
    private ShopEntity shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @Getter
    @JoinColumn(name = "seatId", foreignKey = @ForeignKey(name = "fk_seat_id"))
    private SeatEntity seat;

    @Column(nullable = false)
    private OffsetDateTime enterTime;

    @Setter
    @Column(nullable = true)
    private OffsetDateTime exitTime;


    public Long getSeatId() {
        return seat.getId();
    }

}

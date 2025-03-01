package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.model.TicketTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shop_daily_payment_statistics")
public class ShopDailyPaymentEntity  extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_shop_id"), nullable = false)
    private ShopEntity shop;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int day;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private int totalAmount; // 총 결제액

    @Enumerated(EnumType.STRING)  // Enum으로 ticket_type 지정
    @Column(name = "ticket_type", nullable = false) // insertable=false, updatable=false로 설정하여 서브클래스에서 중복 매핑을 방지
    private TicketTypeEnum ticketType; // PERIOD or TIME


}

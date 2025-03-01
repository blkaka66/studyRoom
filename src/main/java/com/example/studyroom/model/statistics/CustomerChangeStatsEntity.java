package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_change_stats")
public class CustomerChangeStatsEntity extends BaseEntity {

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
    private int totalCustomers;
}

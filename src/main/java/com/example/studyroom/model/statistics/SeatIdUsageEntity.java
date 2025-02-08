package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.SeatEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat_id_usage_statistics")
public class SeatIdUsageEntity extends BaseEntity {

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
    private int hour;

    @ElementCollection
    @CollectionTable(
            name = "seat_id_usage",
            joinColumns = @JoinColumn(name = "seat_usage_id")
    )
    @Column(name = "seat_id")
    private List<Long> activeSeatIds;  // 활성화된 좌석 ID들을 배열로 저장

}

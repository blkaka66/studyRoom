package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

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

    @Column(nullable = false) // 날짜 컬럼 추가
    private LocalDate usageDate;  // 추가된 날짜 컬럼

    // 좌석 ID별 사용 시간 (분 단위)
    @ElementCollection
    @CollectionTable(
            name = "seat_id_usage_duration",
            joinColumns = @JoinColumn(name = "seat_usage_id") //seat_id_usage_statistics테이블의 id를 외래키로 받음 컬럼이름이seat_usage_id인것
    )
    @MapKeyColumn(name = "seat_id") // 좌석 ID를 키로 사용
    @Column(name = "usage_duration") // 사용된 시간 (분 단위)
    private Map<Long, Integer> seatUsageDuration; // 좌석 ID -> 사용 시간 (분)

}

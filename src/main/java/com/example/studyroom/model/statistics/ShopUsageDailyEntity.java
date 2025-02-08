package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
//시간대별 이용률

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shop_usage_daily_statistics")
public class ShopUsageDailyEntity extends BaseEntity {
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
    private int occupancyCount;

    public static ShopUsageDailyEntity from(ShopEntity shop, OffsetDateTime dateTime, int occupancyCount) {
        return ShopUsageDailyEntity.builder()
                .shop(shop)
                .year(dateTime.getYear())
                .month(dateTime.getMonthValue())
                .day(dateTime.getDayOfMonth())
                .dayOfWeek(dateTime.getDayOfWeek())
                .occupancyCount(occupancyCount)
                .build();
    }
}

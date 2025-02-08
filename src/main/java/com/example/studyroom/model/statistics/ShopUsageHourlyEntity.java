package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
//일별 이용률(이땐 사용자 id별로 count)
@Table(name = "shop_usage_hourly_statistics")
public class ShopUsageHourlyEntity extends BaseEntity {
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

    @Column(nullable = false)
    private int occupancyCount;

    public static ShopUsageHourlyEntity from(ShopEntity shop, OffsetDateTime dateTime, int occupancyCount) {
        return ShopUsageHourlyEntity.builder()
                .shop(shop)
                .year(dateTime.getYear())
                .month(dateTime.getMonthValue())
                .day(dateTime.getDayOfMonth())
                .dayOfWeek(dateTime.getDayOfWeek())
                .hour(dateTime.getHour())
                .occupancyCount(occupancyCount)
                .build();
    }
}

package com.example.studyroom.model.statistics;

import com.example.studyroom.model.BaseEntity;
import com.example.studyroom.model.ShopEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
// 한사람당 평균 이용시간 (일별)
@Table(name = "user_avr_usage_statistics")
public class UserAvrUsageEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(name = "fk_shop_id"), nullable = false)
    private ShopEntity shop;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int day;

    @Column(nullable = false) // 총 이용시간
    private int totalUsageMinutes;

    @Column(nullable = false) // 총 회원수
    private int totalUsageUsers;

    @Column(nullable = false) // 평균 이용시간
    private int averageUsageMinutes;
}

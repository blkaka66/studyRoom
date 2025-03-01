package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.statistics.ShopUsageDailyEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;

@Getter
@Builder
public class ShopUsageResponseDto {

    private Long shopId;  // shop의 ID만 포함
    private int year;
    private int month;
    private int day;
    private DayOfWeek dayOfWeek;
    private int occupancyCount;  // 날짜별 점유좌석

    // ShopUsageDailyEntity에서 필요한 정보만 추출하여 DTO로 변환
    public static ShopUsageResponseDto fromEntity(ShopUsageDailyEntity entity) {
        return ShopUsageResponseDto.builder()
                .shopId(entity.getShop().getId())  // shopId만 포함
                .year(entity.getYear())
                .month(entity.getMonth())
                .day(entity.getDay())
                .dayOfWeek(entity.getDayOfWeek())
                .occupancyCount(entity.getOccupancyCount())  // 시간대별 이용률
                .build();
    }
}

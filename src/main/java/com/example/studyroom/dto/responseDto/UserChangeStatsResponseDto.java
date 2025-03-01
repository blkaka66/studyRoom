package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.statistics.CustomerChangeStatsEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;

@Getter
@Builder
public class UserChangeStatsResponseDto {
    private Long shopId;
    private int year;
    private int month;
    private int day;
    private DayOfWeek dayOfWeek;
    private int totalCustomers;

    // CustomerChangeStatsEntity에서 필요한 정보만 추출하여 DTO로 변환
    public static UserChangeStatsResponseDto fromEntity(CustomerChangeStatsEntity entity) {
        return UserChangeStatsResponseDto.builder()
                .shopId(entity.getShop().getId())
                .year(entity.getYear())
                .month(entity.getMonth())
                .day(entity.getDay())
                .dayOfWeek(entity.getDayOfWeek())
                .totalCustomers(entity.getTotalCustomers())
                .build();
    }
}

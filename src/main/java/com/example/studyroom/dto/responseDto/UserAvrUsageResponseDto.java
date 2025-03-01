package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.statistics.UserAvrUsageEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAvrUsageResponseDto {

    private Long shopId;  // shop의 ID만 포함
    private int year;
    private int month;
    private int day;
    private int totalUsageMinutes;  // 총 이용시간
    private int totalUsageUsers;  // 총 회원수
    private double averageUsageMinutes;  // 평균 이용시간

    // UserAvrUsageEntity에서 필요한 정보만 추출하여 DTO로 변환
    public static UserAvrUsageResponseDto fromEntity(UserAvrUsageEntity entity) {
        return UserAvrUsageResponseDto.builder()
                .shopId(entity.getShop().getId())  // shopId만 포함
                .year(entity.getYear())
                .month(entity.getMonth())
                .day(entity.getDay())
                .totalUsageMinutes(entity.getTotalUsageMinutes())
                .totalUsageUsers(entity.getTotalUsageUsers())
                .averageUsageMinutes(entity.getAverageUsageMinutes())
                .build();
    }
}

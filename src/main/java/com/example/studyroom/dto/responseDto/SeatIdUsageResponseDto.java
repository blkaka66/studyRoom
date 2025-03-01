package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.statistics.SeatIdUsageEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.List;

@Getter
@Setter
@Builder
public class SeatIdUsageResponseDto {
    // 내부 DTO 정의
    @Getter
    @Setter
    @Builder
    public static class SeatIdUsageDto {
        private Long id;
        private Long shopId;
        private Map<Long, Integer> seatUsageDuration;
    }
    private List<SeatIdUsageDto> seatIdUsageEntityList;
}

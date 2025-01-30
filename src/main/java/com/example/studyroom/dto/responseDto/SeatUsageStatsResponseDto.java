

package com.example.studyroom.dto.responseDto;

import com.example.studyroom.model.ShopEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder

public class SeatUsageStatsResponseDto {
    private Long id;
    private OffsetDateTime enterTime;
    private OffsetDateTime exitTime;
    private Long shopId;
    private Long seatId;
    private Long memberId;
}

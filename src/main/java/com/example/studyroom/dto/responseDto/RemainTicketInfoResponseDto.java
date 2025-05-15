package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class RemainTicketInfoResponseDto {
    private String seatType;  // "timeSeat" 또는 "periodSeat"
    private String key;       // Redis 키 (시간권용)
    private String value;     // Redis 값 (시간권용)
    private Long ttl;         // 남은 TTL (초 단위, 시간권용)
    private OffsetDateTime endDate;  // 종료 날짜 (기간권용)
    private OffsetDateTime expiresAt;
}

package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder

public class RemainTimeInfoResponseDto {
    private String seatType;  // "timeSeat" 또는 "periodSeat"
    private String key;       // Redis 키
    private String value;     // Redis 값
    private Long ttl;         // 남은 TTL (초 단위)
}

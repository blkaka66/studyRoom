package com.example.studyroom.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder

public class SeatIdUsageRequestDto {
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private long shopId;
}

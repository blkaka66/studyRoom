package com.example.studyroom.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Builder
public class PaymentHistoryDateRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
}

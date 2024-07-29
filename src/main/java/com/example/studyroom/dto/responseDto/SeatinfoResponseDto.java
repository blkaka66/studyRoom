package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class SeatinfoResponseDto {
    private Long id;
    private boolean available;
    private boolean mySeat;
    private boolean onService;
    private int seatCode;
}

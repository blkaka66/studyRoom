package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RoomAndSeatInfoResponseDto { //
    private Long id;
    private String name;
    private boolean onService;
    private List<SeatinfoResponseDto> seats;

    public void setSeats(List<SeatinfoResponseDto> seats) {
        this.seats = seats;
    }
}

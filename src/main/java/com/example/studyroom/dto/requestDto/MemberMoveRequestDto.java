package com.example.studyroom.dto.requestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class MemberMoveRequestDto {
    private int currentRoomCode;
    private int movingRoomCode;
    private int currentSeatNumber;
    private int movingSeatNumber;
}

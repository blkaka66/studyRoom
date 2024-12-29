
package com.example.studyroom.dto.requestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class MemberMoveRequestDto {
    private long roomId;
    private long seatId;
    private int seatCode;
}

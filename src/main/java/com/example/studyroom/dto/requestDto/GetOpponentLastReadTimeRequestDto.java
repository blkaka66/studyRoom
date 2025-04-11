package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOpponentLastReadTimeRequestDto {
    private Long roomId;
    private Long myId;
    private String myType;
}

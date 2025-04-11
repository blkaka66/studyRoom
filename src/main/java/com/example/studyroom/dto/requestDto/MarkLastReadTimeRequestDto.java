package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkLastReadTimeRequestDto {
    private Long roomId;
    private Long userId;
    private String userType;
}

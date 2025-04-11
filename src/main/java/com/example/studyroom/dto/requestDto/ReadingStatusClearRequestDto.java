package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingStatusClearRequestDto {
    private Long roomId;
    private Long userId;
    private String userType; // "user" 또는 "shop"
}

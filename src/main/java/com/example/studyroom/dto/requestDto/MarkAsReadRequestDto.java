package com.example.studyroom.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MarkAsReadRequestDto {
    private Long roomId;
    private String userType;
    private Long userId;
}

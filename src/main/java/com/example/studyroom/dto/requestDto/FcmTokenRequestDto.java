package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenRequestDto {
    private Long requesterId;
    private String requesterType;
    private String token;
}
package com.example.studyroom.dto.requestDto;

import lombok.Getter;

@Getter
public class GetLatestActiveRoomRequestDto {
    private Long requesterId;
    private String requesterType;
    private Long partnerId;
    private String partnerType;
}

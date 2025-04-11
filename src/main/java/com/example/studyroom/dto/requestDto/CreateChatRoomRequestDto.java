package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChatRoomRequestDto {
    private Long requesterId;
    private String requesterType;
    private Long partnerId;
    private String partnerType;
}

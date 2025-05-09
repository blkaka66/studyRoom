package com.example.studyroom.dto.requestDto;

import lombok.Getter;

@Getter
public class EnterChatRoomByIdRequestDto {
    private Long chatRoomId;
    private Long requesterId;
    private String requesterType;
    private String timeStamp;
}

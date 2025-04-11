package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveChatRoomRequestDto {
    private Long roomId;
    private Long requesterId;
    private String requesterType;
}

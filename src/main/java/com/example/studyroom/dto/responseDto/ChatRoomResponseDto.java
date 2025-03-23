package com.example.studyroom.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomResponseDto {
    private Long roomId;
    private Long partnerId;
    private String partnerType;
    private String lastMessage;
    private String lastTimestamp;
}

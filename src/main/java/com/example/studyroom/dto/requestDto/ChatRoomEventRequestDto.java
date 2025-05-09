package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomEventRequestDto {
    private Long roomId;
    private Long senderId;
    private String senderType;
    private Long receiverId;
    private String receiverType;
    private String eventType;
    private String timestamp;
}

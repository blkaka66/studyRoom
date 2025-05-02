package com.example.studyroom.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveChatRoomEventRequestDto {
    private Long roomId;
    private Long senderId;
    private String senderType;
    private Long receiverId;
    private String receiverType;
    private String eventType; // 항상 "LEAVE"
}

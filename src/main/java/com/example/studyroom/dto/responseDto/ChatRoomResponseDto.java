package com.example.studyroom.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomResponseDto {
    private Long roomId;
    private Long senderId;
    private String senderType;
    private Long partnerId;
    private int unreadCount;
    private String partnerType;
    private String lastMessage;
    private String lastTimestamp;
}

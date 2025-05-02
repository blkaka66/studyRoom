package com.example.studyroom.dto.requestDto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDto {
    private Long roomId;
    private Long senderId;         // 보낸 사람 ID
    private String senderType;     // 보낸 사람 타입 (user or admin)
    private Long receiverId;       // 받는 사람 ID
    private String receiverType;   // 받는 사람 타입 (user or admin)
    private String message;        // 메시지 내용
    private String timestamp;      // 메시지 전송 시간
    private String messageType;    // 메시지 타입 (CHAT, LEAVE, TYPING)
}

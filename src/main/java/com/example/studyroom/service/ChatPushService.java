package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatLastReadTimeResponseDto;

public interface ChatPushService {

    void sendChatMessage(ChatMessageResponseDto dto);

    void sendReadTime(Long roomId, ChatLastReadTimeResponseDto payload);

    void sendLeaveMessage(Long roomId, ChatMessageResponseDto dto);

    void sendTypingEvent(Long roomId, Object typingPayload);
}

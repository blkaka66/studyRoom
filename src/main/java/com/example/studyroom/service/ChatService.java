package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ChatHistoryPageRequestDto;
import com.example.studyroom.dto.requestDto.ChatHistoryRequsetDto;
import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatRoomResponseDto;
import com.example.studyroom.model.ChatMessageEntity;

import java.util.List;


public interface ChatService extends BaseService<ChatMessageEntity> {
    void publishChatMessage(ChatMessageRequestDto chatMessage);
    void saveMessage(ChatMessageRequestDto chatMessage); // 메시지 저장
    List<ChatMessageResponseDto> getChatHistory(ChatHistoryRequsetDto dto); // 채팅 기록 조회
    List<ChatRoomResponseDto> getMyChatRooms(Long userId, String userType);

    List<ChatMessageResponseDto> getChatHistoryPaged(ChatHistoryPageRequestDto dto);

}

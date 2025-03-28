package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ChatHistoryPageRequestDto;
import com.example.studyroom.dto.requestDto.ChatHistoryRequsetDto;
import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.requestDto.EnterChatRoomRequestDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatRoomResponseDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.ChatMessageEntity;
import com.example.studyroom.model.ChatRoomEntity;

import java.util.List;


public interface ChatService extends BaseService<ChatMessageEntity> {

    // 채팅방 생성 또는 조회 (엔티티 직접 리턴)
    ChatRoomEntity getOrCreateRoom(Long userId, String userType, Long partnerId, String partnerType);

    // 오버로드: DTO로부터 채팅방 생성 또는 조회
    ChatRoomEntity getOrCreateRoom(ChatMessageRequestDto dto);

    // 채팅 입장 처리용 (roomId 반환)
    Long enterChatRoom(EnterChatRoomRequestDto dto);

    // WebSocket/STOMP 메시지 수신 처리
    void handleMessage(ChatMessageRequestDto chatMessage);

    // Kafka를 통한 메시지 publish (필요 시 직접 호출)
    void publishChatMessage(ChatMessageRequestDto chatMessage);

    // 내가 참여 중인 채팅방 목록 조회
    FinalResponseDto<List<ChatRoomResponseDto>> getMyChatRooms(Long userId, String userType);

    // 채팅 기록 페이징 조회
    FinalResponseDto<List<ChatMessageResponseDto>> getChatHistoryPaged(ChatHistoryPageRequestDto dto);

    //채팅방 입장시 정보조회
    FinalResponseDto<ChatRoomResponseDto> getRoomInfoById(Long roomId);


}

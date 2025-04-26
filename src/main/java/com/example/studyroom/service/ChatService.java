package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.ChatMessageEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


public interface ChatService extends BaseService<ChatMessageEntity> {
    //채팅방생성
    FinalResponseDto<CreateChatRoomResponseDto> createChatRoom(CreateChatRoomRequestDto dto);

    //채팅방 퇴장
    FinalResponseDto<LeaveChatRoomResponseDto> leaveChatRoom(LeaveChatRoomRequestDto dto);


    //현재 진행중인 채팅방 반환(채팅방 입장처리를안하기때문에 enterChatRoom과 비슷하지만 용도가 다름)
    FinalResponseDto<ChatRoomResponseDto> getLatestActiveRoom(GetLatestActiveRoomRequestDto dto);


    // 채팅 입장 처리용 (roomId 반환)
    FinalResponseDto<EnterChatRoomResponseDto> enterChatRoom(EnterChatRoomRequestDto dto, HttpServletRequest request);

    // WebSocket/STOMP 메시지 수신 처리
    void handleMessage(ChatMessageRequestDto chatMessage);


    // 내가 참여 중인 채팅방 목록 조회
    FinalResponseDto<List<ChatRoomResponseDto>> getMyChatRooms(Long requesterId, String requesterType);

    // 채팅 기록 페이징 조회
    FinalResponseDto<List<ChatMessageResponseDto>> getChatHistoryPaged(ChatHistoryPageRequestDto dto);

    //채팅방 입장시 정보조회
    FinalResponseDto<ChatRoomResponseDto> getRoomInfoById(Long roomId);

    //안읽은 메시지 읽었다고 알려주기
    FinalResponseDto<String> markMessagesAsRead(Long roomId, String userType, Long userId);

    //채팅방에 입장중인거 알려주기
    FinalResponseDto<String> markReadingStatus(Long roomId, String userType, Long userId);

    //채팅방 퇴장하는거 알려주기
    FinalResponseDto<String> clearReadingStatus(Long roomId, String userType, Long userId);

    //마지막으로 내가 읽은 시간 표시(채팅읽음표시에 필요)
    FinalResponseDto<String> markLastReadTime(Long roomId, String userType, Long userId);

    //마지막으로 상대가 읽은 시간 가져오기(채팅읽음표시에 필요)
    FinalResponseDto<GetOpponentLastReadTimeResponseDto> getOpponentLastReadTime(Long roomId, String myType, Long myId);
}

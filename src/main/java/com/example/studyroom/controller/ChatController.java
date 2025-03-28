package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.ChatHistoryPageRequestDto;
import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.requestDto.EnterChatRoomRequestDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatRoomResponseDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.ChatRoomEntity;
import com.example.studyroom.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {
    private final ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }


    //  채팅방 입장 (없으면 생성)
    @PostMapping("/chat/enter")
    @ResponseBody
    public FinalResponseDto<Long> enterChatRoom(@RequestBody EnterChatRoomRequestDto dto) {
        Long roomId = chatService.enterChatRoom(dto);
        return FinalResponseDto.successWithData(roomId);
    }

    // 내가 속한 채팅방 목록 조회
    @GetMapping("/chat/rooms")
    @ResponseBody
    public FinalResponseDto<List<ChatRoomResponseDto>> getMyChatRooms(@RequestParam Long userId, @RequestParam String userType) {
        return chatService.getMyChatRooms(userId, userType);
    }

    //  채팅 메시지 전송 (WebSocket)
    @MessageMapping("/chat/send/{roomId}")
    public void sendPrivateMessage(@DestinationVariable Long roomId, ChatMessageRequestDto chatMessage) {
        chatService.handleMessage(chatMessage);
    }

    //  채팅 기록 페이징 조회
    @PostMapping("/chat/history/paged")
    @ResponseBody
    public FinalResponseDto<List<ChatMessageResponseDto>> getChatHistoryPaged(@RequestBody ChatHistoryPageRequestDto dto) {
        return chatService.getChatHistoryPaged(dto);
    }


    // 채팅방 ID로 채팅방 사람 정보 조회
    @GetMapping("/chat/room/{roomId}")
    public FinalResponseDto<ChatRoomResponseDto> getRoomInfo(@PathVariable Long roomId) {

        return chatService.getRoomInfoById(roomId);
    }

}

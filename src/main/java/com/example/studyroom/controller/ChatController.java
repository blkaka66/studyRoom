package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.ChatHistoryPageRequestDto;
import com.example.studyroom.dto.requestDto.ChatHistoryRequsetDto;
import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatRoomResponseDto;
import com.example.studyroom.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

    // ✅ 채팅 기록 조회 (roomId 기준)
    @PostMapping("/chat/history")
    @ResponseBody
    public List<ChatMessageResponseDto> getChatHistory(@RequestBody ChatHistoryRequsetDto dto) {
        return chatService.getChatHistory(dto);
    }

    @PostMapping("/chat/history/paged")
    @ResponseBody
    public List<ChatMessageResponseDto> getChatHistoryPaged(@RequestBody ChatHistoryPageRequestDto dto) {
        return chatService.getChatHistoryPaged(dto);
    }

    // ✅ 메시지 전송 (roomId 기준)
    @MessageMapping("/chat/send/{roomId}")
    public void sendPrivateMessage(@DestinationVariable Long roomId, ChatMessageRequestDto chatMessage) {
        System.out.println("chatMessage"+chatMessage);
        chatService.publishChatMessage(chatMessage);


        // ✅ 채팅방을 구독한 사용자들에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
    }

    @GetMapping("/chat/rooms")
    @ResponseBody
    public List<ChatRoomResponseDto> getMyChatRooms(@RequestParam Long userId, @RequestParam String userType) {
        return chatService.getMyChatRooms(userId, userType);
    }
}


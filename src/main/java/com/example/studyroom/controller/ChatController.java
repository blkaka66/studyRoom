package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
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


    //  채팅방 생성
    @PostMapping("/chat/create")
    @ResponseBody
    public FinalResponseDto<CreateChatRoomResponseDto> createChatRoom(@RequestBody CreateChatRoomRequestDto dto) {
        return chatService.createChatRoom(dto);


    }

    //  채팅방 입장
    @PostMapping("/chat/enter")
    @ResponseBody
    public FinalResponseDto<EnterChatRoomResponseDto> enterChatRoom(@RequestBody EnterChatRoomRequestDto dto) {
        return chatService.enterChatRoom(dto);


    }

    //  채팅방 퇴장
    @PostMapping("/chat/leave")
    @ResponseBody
    public  FinalResponseDto<LeaveChatRoomResponseDto> leaveChatRoom(@RequestBody LeaveChatRoomRequestDto dto) {
        return chatService.leaveChatRoom(dto);


    }


    //기존 채팅방중 활성화된 채팅방 가져오기(단1개여야함)
    @PostMapping("/chat/active-room")
    @ResponseBody
    public FinalResponseDto<ChatRoomResponseDto> getLatestActiveRoom(
            @RequestBody GetLatestActiveRoomRequestDto dto
            ) {

        return chatService.getLatestActiveRoom( dto);

    }


    @PostMapping("/chat/reading")
    @ResponseBody
    public FinalResponseDto<String> markReadingStatus(@RequestBody MarkAsReadRequestDto dto) {
       return  chatService.markReadingStatus(dto.getRoomId(), dto.getUserType(), dto.getUserId());

    }

    @PostMapping("/chat/read")
    @ResponseBody
    public FinalResponseDto<String> markAsRead(@RequestBody MarkAsReadRequestDto dto) {
       return chatService.markMessagesAsRead(dto.getRoomId(), dto.getUserType(), dto.getUserId());
    }

    @PostMapping("/chat/reading-status/clear")
    @ResponseBody
    public FinalResponseDto<String> clearReadingStatus(@RequestBody ReadingStatusClearRequestDto dto) {
       return chatService.clearReadingStatus(dto.getRoomId(), dto.getUserType(), dto.getUserId());

    }

    // 내가 속한 채팅방 목록 조회
    @GetMapping("/chat/rooms")
    @ResponseBody
    public FinalResponseDto<List<ChatRoomResponseDto>> getMyChatRooms(@RequestParam Long requesterId, @RequestParam String requesterType) {
        return chatService.getMyChatRooms(requesterId, requesterType);
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
    @ResponseBody
    public FinalResponseDto<ChatRoomResponseDto> getRoomInfo(@PathVariable Long roomId) {

        return chatService.getRoomInfoById(roomId);
    }



    @PostMapping("/chat/read-status/opponent")
    public FinalResponseDto<GetOpponentLastReadTimeResponseDto> getOpponentReadTime(
            @RequestBody GetOpponentLastReadTimeRequestDto dto) {

        return chatService.getOpponentLastReadTime(dto.getRoomId(), dto.getMyType(), dto.getMyId());


    }

    @PostMapping("/chat/read-status/mark")
    public FinalResponseDto<String> markLastReadTime(
            @RequestBody MarkLastReadTimeRequestDto dto) {

       return chatService.markLastReadTime(dto.getRoomId(), dto.getUserType(), dto.getUserId());

    }

}

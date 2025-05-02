package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatLastReadTimeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatPushServiceImpl implements ChatPushService {

    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseService firebaseService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void sendChatMessage(ChatMessageResponseDto dto) {
        // WebSocket 전송
        messagingTemplate.convertAndSend("/topic/room/" + dto.getRoomId(), dto);

        // 채팅방 리스트에 마지막 메시지 업데이트
        messagingTemplate.convertAndSend("/topic/chat-list/user/" + dto.getReceiverId(), dto);


    }

    @Override
    public void sendReadTime(Long roomId, ChatLastReadTimeResponseDto payload) {
        messagingTemplate.convertAndSend("/topic/chat-read/" + roomId, payload);
    }

    @Override
    public void sendLeaveMessage(Long roomId, ChatMessageResponseDto dto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, dto);
    }

    @Override
    public void sendTypingEvent(Long roomId, Object typingPayload) {
        messagingTemplate.convertAndSend("/topic/typing/" + roomId, typingPayload);
    }
}

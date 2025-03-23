package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisChatSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msg = new String(message.getBody());
            ChatMessageResponseDto chatMessage = objectMapper.readValue(msg, ChatMessageResponseDto.class);
            log.info("Received message from Redis: {}", chatMessage);

            // 실시간으로 WebSocket을 통해 클라이언트에게 전송
            messagingTemplate.convertAndSend("/topic/messages", chatMessage);
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }
}

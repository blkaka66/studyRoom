package com.example.studyroom.consumer;

import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.model.ChatMessageEntity;
import com.example.studyroom.model.ChatRoomEntity;
import com.example.studyroom.repository.ChatRepository;
import com.example.studyroom.repository.ChatRoomRepository;
import com.example.studyroom.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaChatConsumer {

    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // Kafka 메시지 수신 및 DB 저장 처리
    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(String messageJson) {
        log.info("Kafka 메시지 수신: {}", messageJson);

        try {
            ChatMessageRequestDto chatMessage = objectMapper.readValue(messageJson, ChatMessageRequestDto.class);

            ChatRoomEntity room = chatService.getOrCreateRoom(
                    chatMessage.getSenderId(),
                    chatMessage.getSenderType(),
                    chatMessage.getReceiverId(),
                    chatMessage.getReceiverType()
            );

            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setRoom(room);
            entity.setSenderId(chatMessage.getSenderId());
            entity.setSenderType(chatMessage.getSenderType());
            entity.setReceiverId(chatMessage.getReceiverId());
            entity.setReceiverType(chatMessage.getReceiverType());
            entity.setMessage(chatMessage.getMessage());

            LocalDateTime timestamp = LocalDateTime.parse(chatMessage.getTimestamp());
            entity.setTimestamp(timestamp);

            chatRepository.save(entity);
            log.info("Kafka 메시지 저장 완료");
        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패: {}", e.getMessage());
        }
    }
}

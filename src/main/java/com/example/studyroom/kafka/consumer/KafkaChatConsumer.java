package com.example.studyroom.kafka.consumer;

import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.model.ChatMessageEntity;
import com.example.studyroom.model.ChatRoomEntity;
import com.example.studyroom.repository.ChatRepository;
import com.example.studyroom.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class KafkaChatConsumer {

    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    public KafkaChatConsumer(ObjectMapper objectMapper,
                             ChatRepository chatRepository,
                             ChatRoomRepository chatRoomRepository) {
        this.objectMapper = objectMapper;
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(String messageJson) {
        log.info("Kafka 메시지 수신: {}", messageJson);

        try {
            ChatMessageRequestDto chatMessage = objectMapper.readValue(messageJson, ChatMessageRequestDto.class);

            ChatRoomEntity room = chatRoomRepository
                    .findLatestActiveRoomBidirectional(
                            chatMessage.getSenderId(),
                            chatMessage.getSenderType(),
                            chatMessage.getReceiverId(),
                            chatMessage.getReceiverType()
                    )
                    .orElseThrow(() -> {
                        log.error("채팅방이 존재하지 않음: sender {} → receiver {}", chatMessage.getSenderId(), chatMessage.getReceiverId());
                        return new IllegalStateException("채팅방 없음");
                    });

            ChatMessageEntity entity = new ChatMessageEntity();
            entity.setRoom(room);
            entity.setSenderId(chatMessage.getSenderId());
            entity.setSenderType(chatMessage.getSenderType());
            entity.setReceiverId(chatMessage.getReceiverId());
            entity.setReceiverType(chatMessage.getReceiverType());
            entity.setMessage(chatMessage.getMessage());
            entity.setTimestamp(LocalDateTime.parse(chatMessage.getTimestamp()));

            chatRepository.save(entity);
            log.info("Kafka 메시지 저장 완료");

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패: {}", e.getMessage());
        }
    }
}

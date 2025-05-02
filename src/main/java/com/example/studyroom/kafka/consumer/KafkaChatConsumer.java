package com.example.studyroom.kafka.consumer;

import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.requestDto.LeaveChatRoomEventRequestDto;
import com.example.studyroom.model.chat.ChatMessageEntity;
import com.example.studyroom.model.chat.ChatRoomEntity;
import com.example.studyroom.model.chat.MessageType;
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
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .room(room)
                    .senderId(chatMessage.getSenderId())
                    .senderType(chatMessage.getSenderType())
                    .receiverId(chatMessage.getReceiverId())
                    .receiverType(chatMessage.getReceiverType())
                    .message(chatMessage.getMessage())
                    .messageType(MessageType.fromString(chatMessage.getMessageType())) // String -> Enum 변환
                    .timestamp(LocalDateTime.parse(chatMessage.getTimestamp()))
                    .build();
            chatRepository.save(entity);
            log.info("Kafka 메시지 저장 완료");

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "chat-events", groupId = "chat-group")
    public void consumeChatEvent(String eventJson) {
        try {
            LeaveChatRoomEventRequestDto event = objectMapper.readValue(eventJson, LeaveChatRoomEventRequestDto.class);

            if (!"LEAVE".equals(event.getEventType())) {
                return;
            }

            ChatRoomEntity room = chatRoomRepository.findById(event.getRoomId())
                    .orElseThrow(() -> new IllegalStateException("채팅방을 찾을 수 없습니다. roomId: " + event.getRoomId()));

            ChatMessageEntity leaveMessage = ChatMessageEntity.builder()
                    .room(room)
                    .senderId(event.getSenderId())
                    .senderType(event.getSenderType())
                    .receiverId(event.getReceiverId())
                    .receiverType(event.getReceiverType())
                    .message("상대방이 채팅을 종료했습니다.") // 고정 메시지
                    .messageType(MessageType.LEAVE)
                    .timestamp(LocalDateTime.now())
                    .build();

            chatRepository.save(leaveMessage);
            log.info("퇴장 메시지 저장 완료: roomId {}", event.getRoomId());

        } catch (Exception e) {
            log.error("Kafka chat-events 수신 실패: {}", e.getMessage());
        }
    }


}

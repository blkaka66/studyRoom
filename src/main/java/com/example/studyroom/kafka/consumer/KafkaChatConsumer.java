package com.example.studyroom.kafka.consumer;

import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.requestDto.ChatRoomEventRequestDto;

import com.example.studyroom.dto.responseDto.ChatLastReadTimeResponseDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.model.chat.ChatMessageEntity;
import com.example.studyroom.model.chat.ChatRoomEntity;
import com.example.studyroom.model.chat.MessageType;
import com.example.studyroom.repository.ChatRepository;
import com.example.studyroom.repository.ChatRoomRepository;
import com.example.studyroom.service.ChatPushService;
import com.example.studyroom.service.ChatSubscribeService;
import com.example.studyroom.service.FirebaseService;
import com.example.studyroom.type.ToastVariant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KafkaChatConsumer {

    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatPushService chatPushService;
    private final ChatSubscribeService chatSubscribeService;
    private final FirebaseService firebaseService;
    private final StringRedisTemplate redisTemplate;

    public KafkaChatConsumer(ObjectMapper objectMapper,
                             ChatRepository chatRepository,
                             ChatRoomRepository chatRoomRepository, ChatPushService chatPushService,
                             ChatSubscribeService chatSubscribeService, FirebaseService firebaseService, StringRedisTemplate redisTemplate) {
        this.objectMapper = objectMapper;
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatPushService = chatPushService;
        this.firebaseService = firebaseService;
        this.redisTemplate = redisTemplate;
        this.chatSubscribeService = chatSubscribeService;

    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void consume(String messageJson) {
        log.info("Kafka 메시지 수신: {}", messageJson);
        ChatRoomEntity room;
        ChatMessageRequestDto chatMessage;

        try {
            chatMessage = objectMapper.readValue(messageJson, ChatMessageRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 JSON 파싱 실패: {}", messageJson, e);
            return; //이후 로직 진행 불가하므로 리턴
        }

        try {
            room = chatRoomRepository.findLatestActiveRoomBidirectional(
                    chatMessage.getSenderId(),
                    chatMessage.getSenderType(),
                    chatMessage.getReceiverId(),
                    chatMessage.getReceiverType()
            ).orElseThrow(() -> {
                log.error("채팅방이 존재하지 않음: sender {} → receiver {}", chatMessage.getSenderId(), chatMessage.getReceiverId());
                return new IllegalStateException("채팅방 없음");
            });
        } catch (Exception e) {
            log.error("채팅방 조회 실패", e);
            return;//이후 로직 진행 불가하므로 리턴
        }


        Long roomId = room.getId();

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .room(room)
                .senderId(chatMessage.getSenderId())
                .senderType(chatMessage.getSenderType())
                .receiverId(chatMessage.getReceiverId())
                .receiverType(chatMessage.getReceiverType())
                .message(chatMessage.getMessage())
                .messageType((chatMessage.getMessageType())) // String -> Enum 변환
                .timestamp(LocalDateTime.parse(chatMessage.getTimestamp()))
                .build();
        try {
            chatRepository.save(entity);
            log.info("Kafka 메시지 저장 완료");
        } catch (Exception e) {
            log.error("Kafka 메시지 DB 저장 실패", e);
            return;//이후 로직 진행 불가하므로 리턴
        }


        ChatMessageResponseDto response = ChatMessageResponseDto.builder()
                .roomId(roomId)
                .senderId(chatMessage.getSenderId())
                .senderType(chatMessage.getSenderType())
                .receiverId(chatMessage.getReceiverId())
                .receiverType(chatMessage.getReceiverType())
                .message(chatMessage.getMessage())
                .messageType(MessageType.fromString(String.valueOf(chatMessage.getMessageType())))
                .timestamp(chatMessage.getTimestamp())
                .build();

        try {
            chatPushService.sendChatMessage(response);
            log.info("메시지 전송 성공");
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
            return;
        }


        try {
            chatPushService.sendReadTime(roomId, ChatLastReadTimeResponseDto.builder()
                    .userId(chatMessage.getSenderId())
                    .userType(chatMessage.getSenderType())
                    .timestamp(chatMessage.getTimestamp())
                    .build());
        } catch (Exception e) {
            log.error("읽음 시간 전송 실패", e);
        }


        boolean isSubscribed = chatSubscribeService.isSubscribed(
                chatMessage.getReceiverType(), chatMessage.getReceiverId(), roomId);


        if (!isSubscribed) {
            String fcmToken = redisTemplate.opsForValue().get("fcm:" + chatMessage.getReceiverType() + ":" + chatMessage.getReceiverId());
            if (fcmToken != null) {
                Map<String, String> data = new HashMap<>();
                data.put("variant", ToastVariant.CHAT.getValue());
                data.put("roomId", chatMessage.getRoomId().toString());
                try {
                    firebaseService.sendMessageToToken(
                            fcmToken,
                            "새 메시지 도착",
                            chatMessage.getMessage(),
                            data
                    );
                } catch (Exception e) {
                    log.error("FCM 전송 실패", e);
                }
            } else {
                log.error("FCM 토큰 없음: {} - {}", chatMessage.getReceiverType(), chatMessage.getReceiverId());
            }
        }


        String unreadKey = "chat:unread:" + chatMessage.getReceiverType() + ":" + chatMessage.getReceiverId()
                + ":room:" + roomId;
        String readingKey = unreadKey + ":reading";
        Boolean isReading = redisTemplate.hasKey(readingKey);

        if (Boolean.TRUE.equals(isReading)) {
            log.info("상대방이 방 안에 있어 unread 증가 생략");
        } else {
            redisTemplate.opsForValue().increment(unreadKey);
        }


    }

    //일반 채팅기능 제외한 특별한이벤트
    @KafkaListener(topics = "chat-events", groupId = "chat-group")
    public void consumeChatEvent(String eventJson) {
        log.info("Kafka 이벤트 수신: {}", eventJson);
        ChatRoomEventRequestDto event;
        ChatRoomEntity room;
        String eventMessage;
        try {
            event = objectMapper.readValue(eventJson, ChatRoomEventRequestDto.class);
        } catch (Exception e) {
            log.error("Kafka chat-events 수신 실패: {}", e.getMessage());
            return;//이후 로직 진행 불가하므로 리턴
        }
        if ("CHAT".equals(event.getEventType())) {
            return;
        }
        try {
            room = chatRoomRepository.findById(event.getRoomId())
                    .orElseThrow(() -> new IllegalStateException("채팅방을 찾을 수 없습니다. roomId: " + event.getRoomId()));
        } catch (Exception e) {
            log.error("채팅방 조회 실패", e);
            return;//이후 로직 진행 불가하므로 리턴
        }

        eventMessage = switch (event.getEventType()) {
            case "LEAVE" -> "상대방이 채팅을 종료했습니다.";
            case "ENTER" -> "상대방이 입장했습니다";
            default -> "";
        };
        if (!event.getEventType().equals("TYPING")) {
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .room(room)
                    .senderId(event.getSenderId())
                    .senderType(event.getSenderType())
                    .receiverId(event.getReceiverId())
                    .receiverType(event.getReceiverType())
                    .message(eventMessage)
                    .messageType(MessageType.valueOf(event.getEventType()))
                    .timestamp(LocalDateTime.now())
                    .build();
            try {
                chatRepository.save(entity);
                log.info("이벤트 메시지 저장 완료: roomId {}", event.getRoomId());
            } catch (Exception e) {
                log.error("이벤트 메시지 DB 저장 실패", e);
                return; //이후 로직 진행 불가하므로 리턴
            }

        }

        Long roomId = room.getId();
        ChatMessageResponseDto response = ChatMessageResponseDto.builder()
                .roomId(roomId)
                .senderId(event.getSenderId())
                .senderType(event.getSenderType())
                .receiverId(event.getReceiverId())
                .receiverType(event.getReceiverType())
                .message(eventMessage)
                .messageType(MessageType.fromString(event.getEventType()))
                .timestamp(event.getTimestamp())
                .build();

        try {
            chatPushService.sendChatMessage(response);
            log.info("이벤트 메시지 전송 성공 :{}", event.getEventType());
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
            return;
        }


        try {
            chatPushService.sendReadTime(roomId, ChatLastReadTimeResponseDto.builder()
                    .userId(event.getSenderId())
                    .userType(event.getSenderType())
                    .timestamp(event.getTimestamp())
                    .build());
        } catch (Exception e) {
            log.error("읽음 시간 전송 실패", e);
        }


        boolean isSubscribed = chatSubscribeService.isSubscribed(
                event.getReceiverType(), event.getReceiverId(), roomId);

        if (!isSubscribed) {
            String fcmToken = redisTemplate.opsForValue().get("fcm:" + event.getReceiverType() + ":" + event.getReceiverId());
            if (fcmToken != null) {
                Map<String, String> data = new HashMap<>();
                data.put("variant", ToastVariant.CHAT.getValue());
                data.put("roomId", event.getRoomId().toString());
                try {
                    firebaseService.sendMessageToToken(
                            fcmToken,
                            "새 메시지 도착",
                            eventMessage,
                            data
                    );
                } catch (Exception e) {
                    log.error("FCM 전송 실패", e);
                }
            } else {
                log.error("FCM 토큰 없음: {} - {}", event.getReceiverType(), event.getReceiverId());
            }
        }


        String unreadKey = "chat:unread:" + event.getReceiverType() + ":" + event.getReceiverId()
                + ":room:" + roomId;
        String readingKey = unreadKey + ":reading";
        Boolean isReading = redisTemplate.hasKey(readingKey);

        if (Boolean.TRUE.equals(isReading)) {
            log.info("상대방이 방 안에 있어 unread 증가 생략");
        } else {
            redisTemplate.opsForValue().increment(unreadKey);
        }


    }


}

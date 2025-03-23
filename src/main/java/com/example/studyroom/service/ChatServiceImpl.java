package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ChatHistoryPageRequestDto;
import com.example.studyroom.dto.requestDto.ChatHistoryRequsetDto;
import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatRoomResponseDto;
import com.example.studyroom.model.ChatMessageEntity;
import com.example.studyroom.model.ChatRoomEntity;
import com.example.studyroom.repository.ChatRepository;
import com.example.studyroom.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl extends BaseServiceImpl<ChatMessageEntity> implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String CHAT_KEY = "chatroom";

    public ChatServiceImpl(JpaRepository<ChatMessageEntity, Long> repository,
                           ChatRepository chatRepository,
                           ChatRoomRepository chatRoomRepository,
                           StringRedisTemplate redisTemplate,
                           SimpMessagingTemplate messagingTemplate) {
        super(repository);
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void publishChatMessage(ChatMessageRequestDto chatMessage) {
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.opsForList().rightPush(CHAT_KEY, messageJson);
            redisTemplate.expire(CHAT_KEY, 5, TimeUnit.MINUTES);

            messagingTemplate.convertAndSend("/topic/messages", chatMessage);
        } catch (Exception e) {
            log.error("❌ Redis 메시지 발행 실패: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000)
    public void saveMessagesToDB() {
        List<String> messages = redisTemplate.opsForList().range(CHAT_KEY, 0, -1);
        if (messages != null && !messages.isEmpty()) {
            log.info("🔥 Redis에서 {}개의 메시지를 가져와 DB에 저장", messages.size());

            List<ChatMessageEntity> chatEntities = messages.stream()
                    .map(msg -> {
                        try {
                            ChatMessageRequestDto chatMessage = objectMapper.readValue(msg, ChatMessageRequestDto.class);

                            ChatRoomEntity room = getOrCreateRoom(chatMessage);

                            ChatMessageEntity entity = new ChatMessageEntity();
                            entity.setRoom(room);
                            entity.setSenderId(chatMessage.getSenderId());
                            entity.setSenderType(chatMessage.getSenderType());
                            entity.setMessage(chatMessage.getMessage());
                            entity.setReceiverType(chatMessage.getReceiverType());
                            entity.setReceiverId(chatMessage.getReceiverId());

                            // ✅ Z 포함된 ISO 문자열 → LocalDateTime 변환
                            OffsetDateTime odt = OffsetDateTime.parse(chatMessage.getTimestamp());
                            entity.setTimestamp(odt.toLocalDateTime());
                            return entity;

                        } catch (Exception e) {
                            log.error("❌ Redis 메시지 변환 실패: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            chatRepository.saveAll(chatEntities);
            redisTemplate.delete(CHAT_KEY);
        }
    }

    @Override
    public void saveMessage(ChatMessageRequestDto chatMessage) {
        ChatRoomEntity room = getOrCreateRoom(chatMessage);

        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setRoom(room);
        entity.setSenderId(chatMessage.getSenderId());
        entity.setSenderType(chatMessage.getSenderType());
        entity.setMessage(chatMessage.getMessage());
        entity.setTimestamp(LocalDateTime.parse(chatMessage.getTimestamp()));

        chatRepository.save(entity);

        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend("chatroom", messageJson);
        } catch (Exception e) {
            log.error("❌ Redis Pub/Sub 실패: {}", e.getMessage());
        }
    }

    @Override
    public List<ChatMessageResponseDto> getChatHistory(ChatHistoryRequsetDto dto) {
        ChatRoomEntity room = chatRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        return chatRepository.findByRoomOrderByTimestampAsc(room).stream()
                .map(e -> {
                    ChatMessageResponseDto chatMessage = new ChatMessageResponseDto();
                    chatMessage.setSenderId(e.getSenderId());
                    chatMessage.setSenderType(e.getSenderType());
                    chatMessage.setMessage(e.getMessage());
                    chatMessage.setTimestamp(e.getTimestamp().toString());
                    return chatMessage;
                }).collect(Collectors.toList());
    }

    // ✅ 채팅방 없으면 생성
    private ChatRoomEntity getOrCreateRoom(ChatMessageRequestDto chatMessage) {
        return chatRoomRepository.findByUserIdAndUserTypeAndPartnerIdAndPartnerType(
                chatMessage.getSenderId(),
                chatMessage.getSenderType(),
                chatMessage.getReceiverId(),
                chatMessage.getReceiverType()
        ).orElseGet(() -> {
            ChatRoomEntity newRoom = new ChatRoomEntity();
            newRoom.setUserId(chatMessage.getSenderId());
            newRoom.setUserType(chatMessage.getSenderType());
            newRoom.setPartnerId(chatMessage.getReceiverId());
            newRoom.setPartnerType(chatMessage.getReceiverType());
            return chatRoomRepository.save(newRoom);
        });
    }

    @Override
    public List<ChatRoomResponseDto> getMyChatRooms(Long userId, String userType) {
        List<ChatRoomEntity> rooms = chatRoomRepository.findByUserIdAndUserType(userId, userType);

        return rooms.stream().map(room -> {
            ChatRoomResponseDto dto = new ChatRoomResponseDto();
            dto.setRoomId(room.getId());
            dto.setPartnerId(room.getPartnerId());
            dto.setPartnerType(room.getPartnerType());

            // 마지막 메시지 가져오기
            ChatMessageEntity lastMessage = chatRepository.findTopByRoomOrderByTimestampDesc(room)
                    .orElse(null);
            if (lastMessage != null) {
                dto.setLastMessage(lastMessage.getMessage());
                dto.setLastTimestamp(lastMessage.getTimestamp().toString());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageResponseDto> getChatHistoryPaged(ChatHistoryPageRequestDto dto) {
        ChatRoomEntity room = chatRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        Pageable pageable = (Pageable) PageRequest.of(dto.getPage(), dto.getSize(), Sort.by("timestamp").descending());
        Page<ChatMessageEntity> page = chatRepository.findByRoom(room, pageable);

        return page.getContent().stream()
                .map(e -> {
                    ChatMessageResponseDto response = new ChatMessageResponseDto();
                    response.setSenderId(e.getSenderId());
                    response.setSenderType(e.getSenderType());
                    response.setReceiverId(e.getReceiverId());
                    response.setReceiverType(e.getReceiverType());
                    response.setMessage(e.getMessage());
                    response.setTimestamp(e.getTimestamp().toString());
                    return response;
                }).collect(Collectors.toList());

    }

}

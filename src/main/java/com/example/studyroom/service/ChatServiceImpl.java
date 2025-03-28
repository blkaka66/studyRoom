package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.ChatHistoryPageRequestDto;
import com.example.studyroom.dto.requestDto.ChatMessageRequestDto;
import com.example.studyroom.dto.requestDto.EnterChatRoomRequestDto;
import com.example.studyroom.dto.responseDto.ChatMessageResponseDto;
import com.example.studyroom.dto.responseDto.ChatRoomResponseDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.ChatMessageEntity;
import com.example.studyroom.model.ChatRoomEntity;
import com.example.studyroom.repository.ChatRepository;
import com.example.studyroom.repository.ChatRoomRepository;
import com.example.studyroom.type.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl extends BaseServiceImpl<ChatMessageEntity> implements ChatService {

    private final KafkaProducerService kafkaProducerService;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatServiceImpl(JpaRepository<ChatMessageEntity, Long> repository,
                           ChatRepository chatRepository,
                           ChatRoomRepository chatRoomRepository,
                           StringRedisTemplate redisTemplate,
                           SimpMessagingTemplate messagingTemplate,
                           KafkaProducerService kafkaProducerService) {
        super(repository);
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public ChatRoomEntity getOrCreateRoom(Long userId, String userType, Long partnerId, String partnerType) {
        return chatRoomRepository
                .findByUserIdAndUserTypeAndPartnerIdAndPartnerType(userId, userType, partnerId, partnerType)
                .orElseGet(() -> {
                    ChatRoomEntity newRoom = new ChatRoomEntity();
                    newRoom.setUserId(userId);
                    newRoom.setUserType(userType);
                    newRoom.setPartnerId(partnerId);
                    newRoom.setPartnerType(partnerType);
                    newRoom.setCreatedAt(LocalDateTime.now());
                    return chatRoomRepository.save(newRoom);
                });
    }

    @Override
    public ChatRoomEntity getOrCreateRoom(ChatMessageRequestDto dto) {
        return getOrCreateRoom(dto.getSenderId(), dto.getSenderType(), dto.getReceiverId(), dto.getReceiverType());
    }

    @Override
    public Long enterChatRoom(EnterChatRoomRequestDto dto) {
        ChatRoomEntity room = getOrCreateRoom(dto.getUserId(), dto.getUserType(), dto.getPartnerId(), dto.getPartnerType());
        return room.getId();
    }

    @Override
    public void handleMessage(ChatMessageRequestDto chatMessage) {
        try {
            ChatRoomEntity room = getOrCreateRoom(chatMessage);
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            kafkaProducerService.sendChatMessage("chat-messages", messageJson);
            messagingTemplate.convertAndSend("/topic/room/" + room.getId(), chatMessage);
        } catch (Exception e) {
            log.error("채팅 메시지 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    @Override
    public void publishChatMessage(ChatMessageRequestDto chatMessage) {
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            kafkaProducerService.sendChatMessage("chat-messages", messageJson);
            messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), chatMessage);
        } catch (Exception e) {
            log.error("Kafka 메시지 전송 실패: {}", e.getMessage());
        }
    }

    @Override
    public FinalResponseDto<List<ChatRoomResponseDto>> getMyChatRooms(Long userId, String userType) {
        List<ChatRoomEntity> rooms = chatRoomRepository.findRoomsByUserOrPartner(userId, userType);

        List<ChatRoomResponseDto> myRooms = rooms.stream().map(room -> {


            ChatRoomResponseDto.ChatRoomResponseDtoBuilder builder = ChatRoomResponseDto.builder()
                    .roomId(room.getId())
                    .userId(room.getUserId())
                    .userType(room.getUserType())
                    .partnerId(room.getPartnerId())
                    .partnerType(room.getPartnerType());


            chatRepository.findTopByRoomOrderByTimestampDesc(room).ifPresent(lastMessage -> {
                builder.lastMessage(lastMessage.getMessage());
                builder.lastTimestamp(lastMessage.getTimestamp().toString());
            });

            return builder.build();
        }).collect(Collectors.toList());

        return FinalResponseDto.successWithData(myRooms);
    }


    @Override
    public FinalResponseDto<List<ChatMessageResponseDto>> getChatHistoryPaged(ChatHistoryPageRequestDto dto) {
        Optional<ChatRoomEntity> roomOpt = chatRoomRepository.findById(dto.getRoomId());
        if (roomOpt.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by("timestamp").descending());
        Page<ChatMessageEntity> page = chatRepository.findByRoom(roomOpt.get(), pageable);

        if (page.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        List<ChatMessageResponseDto> messages = page.getContent().stream()
                .map(e -> ChatMessageResponseDto.builder()
                        .senderId(e.getSenderId())
                        .senderType(e.getSenderType())
                        .receiverId(e.getReceiverId())
                        .receiverType(e.getReceiverType())
                        .message(e.getMessage())
                        .timestamp(e.getTimestamp().toString())
                        .build())
                .collect(Collectors.toList());

        return FinalResponseDto.successWithData(messages);
    }


    // roomId 기준으로 채팅방 정보 조회
    @Override
    public FinalResponseDto<ChatRoomResponseDto> getRoomInfoById(Long roomId) {
        Optional<ChatRoomEntity> optionalRoom = chatRoomRepository.findById(roomId);

        if (optionalRoom.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        ChatRoomEntity room = optionalRoom.get();
        log.info(room.getPartnerType()+room.getPartnerId()+"room!!");
        ChatRoomResponseDto dto = ChatRoomResponseDto.builder()
                .roomId(room.getId())
                .userId(room.getUserId())
                .userType(room.getUserType())
                .partnerId(room.getPartnerId())
                .partnerType(room.getPartnerType())
                .build();

        return FinalResponseDto.successWithData(dto);
    }

}

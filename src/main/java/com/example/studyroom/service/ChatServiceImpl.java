package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl extends BaseServiceImpl<ChatMessageEntity> implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final KafkaProducerService kafkaProducerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatSubscribeService chatSubscribeService;
    private final FcmTokenService fcmTokenService;

    public ChatServiceImpl(JpaRepository<ChatMessageEntity, Long> repository,
                           ChatRoomRepository chatRoomRepository,
                           ChatRepository chatRepository,
                           KafkaProducerService kafkaProducerService,
                           SimpMessagingTemplate messagingTemplate,
                           StringRedisTemplate redisTemplate,
                           ChatSubscribeService chatSubscribeService,
                           FcmTokenService fcmTokenService) {
        super(repository);
        this.chatRoomRepository = chatRoomRepository;
        this.chatRepository = chatRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.chatSubscribeService = chatSubscribeService;
        this.fcmTokenService = fcmTokenService;
    }

    // 채팅방 생성 (이미 존재하면 예외)

    @Override
    public FinalResponseDto<CreateChatRoomResponseDto> createChatRoom(CreateChatRoomRequestDto dto) {
        Optional<ChatRoomEntity> existingOpt = chatRoomRepository.findLatestActiveRoomBidirectional(
                dto.getRequesterId(), dto.getRequesterType(),
                dto.getPartnerId(), dto.getPartnerType()
        );

        if (existingOpt.isPresent()) {
            ChatRoomEntity existing = existingOpt.get();

            boolean requesterIsUser = existing.getSenderId().equals(dto.getRequesterId())
                    && existing.getSenderType().equals(dto.getRequesterType());

            boolean requesterClosed = requesterIsUser
                    ? Boolean.TRUE.equals(existing.getSenderClosed())
                    : Boolean.TRUE.equals(existing.getPartnerClosed());

            boolean partnerClosed = requesterIsUser
                    ? Boolean.TRUE.equals(existing.getPartnerClosed())
                    : Boolean.TRUE.equals(existing.getSenderClosed());

            // 둘 중 하나라도 나갔으면 새 방 생성
            if (!requesterClosed && !partnerClosed) {
                return FinalResponseDto.failure(ApiResult.ALREADY_EXIST_ROOM);
            }
        }

        ChatRoomEntity room = new ChatRoomEntity();
        room.setSenderId(dto.getRequesterId());
        room.setSenderType(dto.getRequesterType());
        room.setPartnerId(dto.getPartnerId());
        room.setPartnerType(dto.getPartnerType());
        room.setCreatedAt(LocalDateTime.now());
        room.setSenderClosed(false);
        room.setPartnerClosed(false);

        chatRoomRepository.save(room);
        CreateChatRoomResponseDto response = CreateChatRoomResponseDto.builder()
                .chatRoomId(room.getId())
                .build();

        return FinalResponseDto.successWithData(response);
    }


    @Override
    public FinalResponseDto<ChatRoomResponseDto> getLatestActiveRoom(GetLatestActiveRoomRequestDto dto) {
        Optional<ChatRoomEntity> existing = chatRoomRepository.findLatestActiveRoomBidirectional(dto.getRequesterId(), dto.getRequesterType(), dto.getPartnerId(), dto.getPartnerType());


        if (existing.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        ChatRoomEntity room = existing.get();

        // 양쪽 모두 나가지 않은 상태여야 함
        boolean requesterIsUser = room.getSenderId().equals(dto.getRequesterId()) && room.getSenderType().equals(dto.getRequesterType());
        boolean requesterClosed = requesterIsUser ? Boolean.TRUE.equals(room.getSenderClosed()) : Boolean.TRUE.equals(room.getPartnerClosed());
        boolean partnerClosed = requesterIsUser ? Boolean.TRUE.equals(room.getPartnerClosed()) : Boolean.TRUE.equals(room.getSenderClosed());

        if (requesterClosed || partnerClosed) {
            return FinalResponseDto.failure(ApiResult.ALREADY_CLOSED_ROOM);
        }

        ChatRoomResponseDto result = ChatRoomResponseDto.builder()
                .roomId(room.getId())
                .senderId(room.getSenderId())
                .senderType(room.getSenderType())
                .partnerId(room.getPartnerId())
                .partnerType(room.getPartnerType())
                .build();

        return FinalResponseDto.successWithData(result);
    }


    // 채팅방 입장 (입장 기록 갱신, 나간 경우 입장 불가)
    @Override
    public FinalResponseDto<EnterChatRoomResponseDto> enterChatRoom(EnterChatRoomRequestDto dto) {
        Optional<ChatRoomEntity> optionalRoom = chatRoomRepository.findLatestActiveRoomBidirectional(
                dto.getRequesterId(), dto.getRequesterType(), dto.getPartnerId(), dto.getPartnerType());

        if (optionalRoom.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        ChatRoomEntity room = optionalRoom.get();

        boolean isUser = room.getSenderId().equals(dto.getRequesterId()) && room.getSenderType().equals(dto.getRequesterType());
        boolean isClosed = isUser ? Boolean.TRUE.equals(room.getSenderClosed()) : Boolean.TRUE.equals(room.getPartnerClosed());

        if (isClosed) {
            log.info("입장 실패: 이미 방에서 퇴장한 사용자");
            return FinalResponseDto.failure(ApiResult.ALREADY_CLOSED_ROOM);
        }


        chatRoomRepository.save(room);
//        markMessagesAsRead(room.getId(), dto.getRequesterType(), dto.getRequesterId());
//        markReadingStatus(room.getId(), dto.getRequesterType(), dto.getRequesterId());


        EnterChatRoomResponseDto response = EnterChatRoomResponseDto.builder()
                .chatRoomId(room.getId())
                .build();

        return FinalResponseDto.successWithData(response);
    }

    // 퇴장 처리
    @Override
    public FinalResponseDto<LeaveChatRoomResponseDto> leaveChatRoom(LeaveChatRoomRequestDto dto) {
        Optional<ChatRoomEntity> roomOpt = chatRoomRepository.findById(dto.getRoomId());
        if (roomOpt.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        ChatRoomEntity room = roomOpt.get();

        boolean isUser = room.getSenderId().equals(dto.getRequesterId()) && room.getSenderType().equals(dto.getRequesterType());

        if (isUser) {
            room.setSenderClosed(true);
        } else {
            room.setPartnerClosed(true);
        }

        chatRoomRepository.save(room);
        chatSubscribeService.unsubscribe(dto.getRequesterId(), dto.getRoomId());

        LeaveChatRoomResponseDto response = LeaveChatRoomResponseDto.builder()
                .chatRoomId(room.getId())
                .build();

        return FinalResponseDto.successWithData(response);
    }

    // 메세지 처리
    @Override
    public void handleMessage(ChatMessageRequestDto chatMessage) {
        try {
            ChatRoomEntity room = chatRoomRepository.findById(chatMessage.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

            String messageJson = objectMapper.writeValueAsString(chatMessage);
            kafkaProducerService.sendChatMessage("chat-messages", messageJson);
            messagingTemplate.convertAndSend("/topic/room/" + room.getId(), chatMessage);

            boolean isSubscribed = chatSubscribeService.isSubscribed(
                    chatMessage.getReceiverId(), chatMessage.getRoomId());
        
            if (!isSubscribed) {
                fcmTokenService.sendChatNotification(
                        chatMessage.getReceiverId(),
                        chatMessage.getReceiverType(),
                        chatMessage.getMessage() // 실제 메시지 내용
                );
            }
            // 읽음 처리
            String unreadKey = "chat:unread:" + chatMessage.getReceiverType() + ":" + chatMessage.getReceiverId() + ":room:" + room.getId();
            String readingKey = unreadKey + ":reading";
            Boolean isReading = redisTemplate.hasKey(readingKey);

            if (Boolean.TRUE.equals(isReading)) {
                log.info("상대방이 방 안에 있어 unread 증가 생략");
            } else {
                redisTemplate.opsForValue().increment(unreadKey);
            }
        } catch (Exception e) {
            log.error("메시지 처리 오류", e);
        }
    }

    @Override
    public FinalResponseDto<List<ChatRoomResponseDto>> getMyChatRooms(Long requesterId, String requesterType) {
        List<ChatRoomEntity> rooms = chatRoomRepository.findRoomsBySenderOrPartner(requesterId, requesterType);

        List<ChatRoomResponseDto> myRooms = rooms.stream()
                .filter(room -> {
                    if (room.getSenderId().equals(requesterId) && room.getSenderType().equals(requesterType)) {
                        return !Boolean.TRUE.equals(room.getSenderClosed());
                    } else {
                        return !Boolean.TRUE.equals(room.getPartnerClosed());
                    }
                })
                .map(room -> {
                    ChatRoomResponseDto.ChatRoomResponseDtoBuilder builder = ChatRoomResponseDto.builder()
                            .roomId(room.getId())
                            .senderId(room.getSenderId())
                            .senderType(room.getSenderType())
                            .partnerId(room.getPartnerId())
                            .partnerType(room.getPartnerType());

                    chatRepository.findTopByRoomOrderByTimestampDesc(room)
                            .ifPresent(msg -> {
                                builder.lastMessage(msg.getMessage());
                                builder.lastTimestamp(msg.getTimestamp().toString());
                            });

                    String unreadKey = "chat:unread:" + requesterType + ":" + requesterId + ":room:" + room.getId();
                    String unreadCount = redisTemplate.opsForValue().get(unreadKey);
                    builder.unreadCount(unreadCount != null ? Integer.parseInt(unreadCount) : 0);

                    return builder.build();
                })
                .collect(Collectors.toList());

        return FinalResponseDto.successWithData(myRooms);
    }

    @Override
    public FinalResponseDto<String> markMessagesAsRead(Long roomId, String userType, Long userId) {
        String unreadKey = "chat:unread:" + userType + ":" + userId + ":room:" + roomId;
        redisTemplate.delete(unreadKey);
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<String> markReadingStatus(Long roomId, String userType, Long userId) {
        String readingKey = "chat:unread:" + userType + ":" + userId + ":room:" + roomId + ":reading";
        redisTemplate.opsForValue().set(readingKey, "1");
        // 구독 상태도 같이 저장
        chatSubscribeService.subscribe(userId, roomId);
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<String> clearReadingStatus(Long roomId, String userType, Long userId) {
        String key = "chat:unread:" + userType + ":" + userId + ":room:" + roomId + ":reading";
        redisTemplate.delete(key);
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<String> markLastReadTime(Long roomId, String userType, Long userId) {
        String key = "chat:lastRead:" + roomId + ":" + userType + ":" + userId;
        String timestamp = LocalDateTime.now().toString();
        redisTemplate.opsForValue().set(key, timestamp);
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<GetOpponentLastReadTimeResponseDto> getOpponentLastReadTime(Long roomId, String myType, Long myId) {
        ChatRoomEntity room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        String opponentType;
        Long opponentId;

        if (room.getSenderId().equals(myId) && room.getSenderType().equals(myType)) {
            opponentId = room.getPartnerId();
            opponentType = room.getPartnerType();
        } else {
            opponentId = room.getSenderId();
            opponentType = room.getSenderType();
        }

        String key = "chat:lastRead:" + roomId + ":" + opponentType + ":" + opponentId;
        String lastReadTimestamp = redisTemplate.opsForValue().get(key);
        GetOpponentLastReadTimeResponseDto response = GetOpponentLastReadTimeResponseDto.builder()
                .lastReadTimestamp(lastReadTimestamp)
                .build();

        return FinalResponseDto.successWithData(response);
    }


    @Override
    public FinalResponseDto<ChatRoomResponseDto> getRoomInfoById(Long roomId) {
        ChatRoomEntity room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        return FinalResponseDto.successWithData(ChatRoomResponseDto.builder()
                .roomId(room.getId())
                .senderId(room.getSenderId())
                .senderType(room.getSenderType())
                .partnerId(room.getPartnerId())
                .partnerType(room.getPartnerType())
                .build());
    }

    @Override
    public FinalResponseDto<List<ChatMessageResponseDto>> getChatHistoryPaged(ChatHistoryPageRequestDto dto) {
        ChatRoomEntity room = chatRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        Pageable pageable = PageRequest.of(Math.max(0, dto.getPage()), Math.max(1, dto.getSize()), Sort.by("timestamp").descending());
        Page<ChatMessageEntity> page = chatRepository.findByRoom(room, pageable);

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
}

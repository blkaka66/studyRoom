package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.*;
import com.example.studyroom.dto.responseDto.*;
import com.example.studyroom.model.chat.ChatMessageEntity;
import com.example.studyroom.model.chat.ChatRoomEntity;
import com.example.studyroom.kafka.producer.KafkaProducerService;
import com.example.studyroom.model.chat.MessageType;
import com.example.studyroom.repository.ChatRepository;
import com.example.studyroom.repository.ChatRoomRepository;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.type.ApiResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
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
    private final FirebaseService firebaseService;
    private final ChatPushService chatPushService;

    public ChatServiceImpl(JpaRepository<ChatMessageEntity, Long> repository,
                           ChatRoomRepository chatRoomRepository,
                           ChatRepository chatRepository,
                           KafkaProducerService kafkaProducerService,
                           SimpMessagingTemplate messagingTemplate,
                           StringRedisTemplate redisTemplate,
                           ChatSubscribeService chatSubscribeService,
                           FcmTokenService fcmTokenService, JwtUtil jwtUtil,
                           FirebaseService firebaseService, ChatPushService chatPushService) {
        super(repository);
        this.chatRoomRepository = chatRoomRepository;
        this.chatRepository = chatRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.chatSubscribeService = chatSubscribeService;
        this.firebaseService = firebaseService;
        this.chatPushService = chatPushService;
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
    public FinalResponseDto<EnterChatRoomResponseDto> enterChatRoom(EnterChatRoomRequestDto dto, HttpServletRequest request) {


        Optional<ChatRoomEntity> optionalRoom = chatRoomRepository.findLatestActiveRoomBidirectional(
                dto.getRequesterId(), dto.getRequesterType(), dto.getPartnerId(), dto.getPartnerType());

        if (optionalRoom.isEmpty()) {
            log.info("방이없음!!");
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


        EnterChatRoomResponseDto response = EnterChatRoomResponseDto.builder()
                .chatRoomId(room.getId())
                .build();

        return FinalResponseDto.successWithData(response);
    }

    @Override
    public FinalResponseDto<EnterChatRoomResponseDto> validateAndEnterOldChatRoom(EnterChatRoomByIdRequestDto dto) {

        Optional<ChatRoomEntity> optionalRoom = chatRoomRepository.findById(dto.getChatRoomId());

        if (optionalRoom.isEmpty()) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        ChatRoomEntity room = optionalRoom.get();

        boolean isSender = room.getSenderId().equals(dto.getRequesterId()) && room.getSenderType().equals(dto.getRequesterType());
        boolean isPartner = room.getPartnerId().equals(dto.getRequesterId()) && room.getPartnerType().equals(dto.getRequesterType());

        if (!isSender && !isPartner) {
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }

        boolean iAmClosed = isSender ? Boolean.TRUE.equals(room.getSenderClosed()) : Boolean.TRUE.equals(room.getPartnerClosed());

        if (iAmClosed) {
            return FinalResponseDto.failure(ApiResult.ALREADY_CLOSED_ROOM);
        }

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

        boolean isUser = room.getSenderId().equals(dto.getRequesterId()) &&
                room.getSenderType().equals(dto.getRequesterType());

        if (isUser) {
            room.setSenderClosed(true);
        } else {
            room.setPartnerClosed(true);
        }

        chatRoomRepository.save(room);
        chatSubscribeService.unsubscribe(dto.getRequesterType(), dto.getRequesterId(), dto.getRoomId());

        LeaveChatRoomResponseDto response = LeaveChatRoomResponseDto.builder()
                .chatRoomId(room.getId())
                .build();
        LocalDateTime now = LocalDateTime.now();
        //퇴장 메시지 전송
        Long receiverId;
        String receiverType;

        if (isUser) {
            receiverId = room.getPartnerId();
            receiverType = room.getPartnerType();
        } else {
            receiverId = room.getSenderId();
            receiverType = room.getSenderType();
        }
        sendLeaveEvent(
                dto.getRequesterId(),
                dto.getRequesterType(),
                receiverId,
                receiverType,
                room.getId(),
                now
        );


        return FinalResponseDto.successWithData(response);
    }

    private void sendLeaveEvent(Long senderId, String senderType, Long receiverId, String receiverType, Long roomId, LocalDateTime now) {
        try {
            LeaveChatRoomEventRequestDto leaveEvent = new LeaveChatRoomEventRequestDto();
            leaveEvent.setSenderId(senderId);
            leaveEvent.setSenderType(senderType);
            leaveEvent.setReceiverId(receiverId);
            leaveEvent.setReceiverType(receiverType);
            leaveEvent.setRoomId(roomId);
            leaveEvent.setEventType("LEAVE");

            String jsonMessage = objectMapper.writeValueAsString(leaveEvent);

            kafkaProducerService.sendChatMessage("chat-events", jsonMessage);

            ChatMessageResponseDto response = ChatMessageResponseDto.builder()
                    .roomId(roomId)
                    .senderId(senderId)
                    .senderType(senderType)
                    .receiverId(receiverId)
                    .receiverType(receiverType)
                    .message("상대방이 채팅을 종료했습니다.")
                    .messageType("LEAVE")
                    .timestamp(String.valueOf(now))
                    .build();
            chatPushService.sendChatMessage(response);
        } catch (JsonProcessingException e) {
            log.error("LEAVE 이벤트 Kafka 전송 중 JSON 변환 실패", e);
            throw new RuntimeException("Kafka 전송 실패: JSON 직렬화 오류", e);
        }
    }

    @Override
    public void handleTypingEvent(ChatMessageRequestDto typingMessage) {
        ChatMessageResponseDto response = ChatMessageResponseDto.builder()
                .roomId(typingMessage.getRoomId())
                .senderId(typingMessage.getSenderId())
                .senderType(typingMessage.getSenderType())
                .receiverId(typingMessage.getReceiverId())
                .receiverType(typingMessage.getReceiverType())
                .message("") // 메시지는 빈값
                .messageType("TYPING")
                .timestamp(LocalDateTime.now().toString())
                .build();

        chatPushService.sendChatMessage(response);
    }

    // 메세지 처리
    @Override
    public void handleMessage(ChatMessageRequestDto chatMessage) {
        try {
            ChatRoomEntity room = chatRoomRepository.findById(chatMessage.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

            String messageJson = objectMapper.writeValueAsString(chatMessage);
            kafkaProducerService.sendChatMessage("chat-messages", messageJson);

            ChatMessageResponseDto response = ChatMessageResponseDto.builder()
                    .roomId(room.getId())
                    .senderId(chatMessage.getSenderId())
                    .senderType(chatMessage.getSenderType())
                    .receiverId(chatMessage.getReceiverId())
                    .receiverType(chatMessage.getReceiverType())
                    .message(chatMessage.getMessage())
                    .messageType(chatMessage.getMessageType())
                    .timestamp(chatMessage.getTimestamp())
                    .build();

            chatPushService.sendChatMessage(response);

//            ChatListUpdateResponseDto payload = ChatListUpdateResponseDto.builder()
//                    .roomId(chatMessage.getRoomId())
//                    .message(chatMessage.getMessage())
//                    .timestamp(chatMessage.getTimestamp())
//                    .build();

            chatPushService.sendReadTime(room.getId(), ChatLastReadTimeResponseDto.builder()
                    .userId(chatMessage.getSenderId())
                    .userType(chatMessage.getSenderType())
                    .timestamp(chatMessage.getTimestamp())
                    .build());

            boolean isSubscribed = chatSubscribeService.isSubscribed(
                    chatMessage.getReceiverType(), chatMessage.getReceiverId(), chatMessage.getRoomId());

            if (!isSubscribed) {
                String fcmToken = redisTemplate.opsForValue().get("fcm:" + chatMessage.getReceiverType() + ":" + chatMessage.getReceiverId());
                if (fcmToken != null) {
                    firebaseService.sendMessageToToken(
                            fcmToken,
                            "새 메시지 도착",
                            chatMessage.getMessage(),
                            null
                    );
                }
            }

            String unreadKey = "chat:unread:" + chatMessage.getReceiverType() + ":" + chatMessage.getReceiverId()
                    + ":room:" + room.getId();
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
        chatSubscribeService.subscribe(userType, userId, roomId);
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<String> clearReadingStatus(Long roomId, String userType, Long userId) {
        String key = "chat:unread:" + userType + ":" + userId + ":room:" + roomId + ":reading";
        redisTemplate.delete(key);
        // 구독 상태도 해제
        chatSubscribeService.unsubscribe(userType, userId, roomId);
        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<String> markLastReadTime(Long roomId, String userType, Long userId) {
        String key = "chat:lastRead:" + roomId + ":" + userType + ":" + userId;
        String timestamp = LocalDateTime.now().toString();
        redisTemplate.opsForValue().set(key, timestamp);

        // 여기서 WebSocket으로 상대방에게 알림 전송
        ChatLastReadTimeResponseDto payload = ChatLastReadTimeResponseDto.builder()
                .userId(userId)
                .userType(userType)
                .timestamp(timestamp)
                .build();

        // 브로드캐스트 추가
        messagingTemplate.convertAndSend("/topic/chat-read/" + roomId, payload);

        return FinalResponseDto.success();
    }

    @Override
    public FinalResponseDto<GetOpponentLastReadTimeResponseDto> getOpponentLastReadTime(Long roomId, String myType, Long myId) {
        ChatRoomEntity room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        String opponentType;
        Long opponentId;
        log.info("getOpponentLastReadTime Dto :{} ,:{} , :{}", roomId, myType, myId);
        if ("shop".equals(myType)) {
            opponentId = room.getSenderId();
            opponentType = "user";
        } else {
            opponentId = room.getPartnerId();
            opponentType = "shop";
        }


        String key = "chat:lastRead:" + roomId + ":" + opponentType + ":" + opponentId;
        log.info("getOpponentLastReadTime key :{}", key);
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
                .partner_closed(room.getPartnerClosed())
                .sender_closed(room.getSenderClosed())
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
                        .messageType(e.getMessageType().toString())
                        .build())
                .collect(Collectors.toList());

        return FinalResponseDto.successWithData(messages);
    }


}

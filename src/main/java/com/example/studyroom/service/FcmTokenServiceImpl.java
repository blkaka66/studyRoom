package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.type.ApiResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FcmTokenServiceImpl implements FcmTokenService {
    private final StringRedisTemplate redisTemplate;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;

    public FcmTokenServiceImpl(StringRedisTemplate redisTemplate, ShopRepository shopRepository, MemberRepository memberRepository) {
        this.redisTemplate = redisTemplate;
        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;
    }

    private String makeKey(Long requesterId, String requesterType) {
        return "fcm:" + requesterType + ":" + requesterId;
    }

    @Override
    public FinalResponseDto<String> saveToken(Long requesterId, String requesterType, String token) {
        String key = makeKey(requesterId, requesterType);
        redisTemplate.opsForValue().set(key, token);
        log.info("Redis에 저장된 FCM 토큰: {} -> {}", key, token);
        return FinalResponseDto.success();
    }

    // 필요한 경우 알림 보낼 때 꺼내 쓸 수 있음
    public String getToken(Long requesterId, String requesterType) {
        String key = makeKey(requesterId, requesterType);
        return redisTemplate.opsForValue().get(key);
    }

//    public void sendTestNotification(Long requesterId, String requesterType) {
//        String token = getToken(requesterId, requesterType);
//        if (token == null) {
//            log.warn("FCM 토큰이 존재하지 않습니다. requesterId: {}, type: {}", requesterId, requesterType);
//            return;
//        }
//
//        Message message = Message.builder()
//                .setToken(token)
//                .setNotification(Notification.builder()
//                        .setTitle("테스트 알림")
//                        .setBody("정상적으로 수신되었는지 확인해주세요.")
//                        .build())
//                .build();
//
//        try {
//            String response = FirebaseMessaging.getInstance().send(message);
//            log.info("FCM 알림 전송 성공: {}", response);
//        } catch (FirebaseMessagingException e) {
//            log.error("FCM 알림 전송 실패: {}", e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void sendChatNotification(Long senderId, Long receiverId, String receiverType, String messageContent) {
//        String token = getToken(receiverId, receiverType);
//
//        if (token == null) {
//            log.warn("FCM 토큰이 존재하지 않습니다. receiverId: {}, type: {}", receiverId, receiverType);
//            return;
//        }
//
//        String senderName = "알 수 없음";
//        log.info("sendChatNotification receiverType : {}, messageContent : {}", receiverType, messageContent);
//        if (receiverType.equals("user")) {
//            Optional<ShopEntity> shop = shopRepository.findById(senderId);
//            if (shop.isEmpty()) {
//                log.warn("점주가 존재하지 않습니다. senderId: {}", senderId);
//                return;
//            }
//            senderName = shop.get().getName();
//        } else if (receiverType.equals("shop")) {
//            Optional<MemberEntity> member = memberRepository.findById(senderId);
//            if (member.isEmpty()) {
//                log.warn("회원이 존재하지 않습니다. senderId: {}", senderId);
//                return;
//            }
//            senderName = member.get().getName();
//        }
//
//        Message message = Message.builder()
//                .setToken(token)
//                .setNotification(Notification.builder()
//                        .setTitle(senderName)
//                        .setBody(messageContent)
//                        .build())
//                .build();
//
//        try {
//            String response = FirebaseMessaging.getInstance().send(message);
//            log.info("FCM 알림 전송 성공: {}", response);
//        } catch (FirebaseMessagingException e) {
//            log.error("FCM 알림 전송 실패: {}", e.getMessage(), e);
//        }
//    }

}

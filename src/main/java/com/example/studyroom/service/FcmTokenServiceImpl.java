package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.type.ApiResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FcmTokenServiceImpl implements FcmTokenService {
    private final StringRedisTemplate redisTemplate;

    public FcmTokenServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
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

    public void sendTestNotification(Long requesterId, String requesterType) {
        String token = getToken(requesterId, requesterType);
        if (token == null) {
            log.warn("FCM 토큰이 존재하지 않습니다. requesterId: {}, type: {}", requesterId, requesterType);
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("테스트 알림")
                        .setBody("정상적으로 수신되었는지 확인해주세요.")
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 전송 실패: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendChatNotification(Long receiverId, String receiverType, String messageContent) {
        String token = getToken(receiverId, receiverType);
        if (token == null) {
            log.warn("FCM 토큰이 존재하지 않습니다. receiverId: {}, type: {}", receiverId, receiverType);
            return;
        }

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("새 메시지 도착")
                        .setBody(messageContent)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 전송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 전송 실패: {}", e.getMessage(), e);
        }
    }
}

package com.example.studyroom.kafka.consumer;

import com.example.studyroom.service.FirebaseService;
import com.example.studyroom.type.ToastVariant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component

public class MemberSignUpConsumer {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final FirebaseService firebaseService;

    public MemberSignUpConsumer(FirebaseService firebaseService, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {

        this.firebaseService = firebaseService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "member-signup-notice", groupId = "member-signup-group")
    public void listen(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            Long shopId = node.get("shopId").asLong();


            String fcmToken = redisTemplate.opsForValue().get("fcm:shop:" + shopId);

            if (fcmToken != null) {
                Map<String, String> data = new HashMap<>();
                data.put("variant", ToastVariant.INFO.getValue());
                firebaseService.sendMessageToToken(
                        fcmToken,
                        "신규 회원 알림",
                        "신규 회원이 가입했습니다.",
                        data
                );
                log.info(" 푸시 알림 전송 완료 - userId: {}", shopId);
            } else {
                log.warn(" FCM 토큰 없음 - userId: {}", shopId);
            }

        } catch (Exception e) {
            log.error(" Kafka 메시지 처리 실패", e);
        }
    }
}

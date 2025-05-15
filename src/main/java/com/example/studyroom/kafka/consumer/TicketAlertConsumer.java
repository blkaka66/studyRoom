package com.example.studyroom.kafka.consumer;

import com.example.studyroom.service.FirebaseService;
import com.example.studyroom.type.ToastVariant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TicketAlertConsumer {

    private final FirebaseService firebaseService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public TicketAlertConsumer(FirebaseService firebaseService,
                               RedisTemplate<String, String> redisTemplate,
                               ObjectMapper objectMapper) {
        this.firebaseService = firebaseService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    @KafkaListener(topics = "ticket-expiration-warning", groupId = "ticket-alert-group")
    public void listen(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            Long userId = node.get("userId").asLong();


            String fcmToken = redisTemplate.opsForValue().get("fcm:user:" + userId);

            if (fcmToken != null) {
                Map<String, String> data = new HashMap<>();
                data.put("variant", ToastVariant.WARNING.getValue());
                firebaseService.sendMessageToToken(
                        fcmToken,
                        "티켓 만료 알림",
                        "티켓 만료 하루 전입니다.",
                        data
                );
                log.info(" 푸시 알림 전송 완료 - userId: {}", userId);
            } else {
                log.warn(" FCM 토큰 없음 - userId: {}", userId);
            }

        } catch (Exception e) {
            log.error(" Kafka 메시지 처리 실패", e);
        }
    }
}

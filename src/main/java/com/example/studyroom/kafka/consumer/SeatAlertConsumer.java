package com.example.studyroom.kafka.consumer;

import com.example.studyroom.service.FirebaseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

@Component
@Slf4j
public class SeatAlertConsumer {

    private final FirebaseService firebaseService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public SeatAlertConsumer(FirebaseService firebaseService,
                             RedisTemplate<String, String> redisTemplate,
                             ObjectMapper objectMapper) {
        this.firebaseService = firebaseService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "seat-expiration-warning", groupId = "seat-alert-group")
    public void listen(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            Long userId = node.get("userId").asLong();
            Long seatId = node.get("seatId").asLong();

            String fcmToken = redisTemplate.opsForValue().get("fcm:user:" + userId);

            if (fcmToken != null) {
                firebaseService.sendMessageToToken(
                        fcmToken,
                        "이용 종료 알림",
                        "자리 이용 시간이 10분 남았습니다.",
                        null
                );
                log.info(" 푸시 알림 전송 완료 - userId: {}, seatId: {}", userId, seatId);
            } else {
                log.warn(" FCM 토큰 없음 - userId: {}", userId);
            }

        } catch (Exception e) {
            log.error(" Kafka 메시지 처리 실패", e);
        }
    }
}

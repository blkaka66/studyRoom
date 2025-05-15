package com.example.studyroom.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TicketAlertProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TicketAlertProducer(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTicketExpirationWarning(Long userId, Long shopId, OffsetDateTime sendTime, String ticketType) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("shopId", shopId);
            payload.put("sendTime", sendTime.toString());
            payload.put("ticketType", ticketType);

            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send("ticket-expiration-warning", String.valueOf(shopId), message);
            log.info("📤 Kafka 전송 완료 - ticketAlert: {}", message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }

}

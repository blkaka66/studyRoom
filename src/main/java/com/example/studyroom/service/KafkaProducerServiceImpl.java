package com.example.studyroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendChatMessage(String topic, String messageJson) {
        log.info("🔥 Kafka에 메시지 전송 시도: {}", messageJson); // 로그 추가
        kafkaTemplate.send(topic, messageJson);
    }
}

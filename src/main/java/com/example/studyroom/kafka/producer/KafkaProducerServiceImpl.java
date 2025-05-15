package com.example.studyroom.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendChatMessage(String topic, long roomId, String messageJson) {

        log.info("🔥 Kafka 전송 시도 | topic: {} | roomId: {} | messageJson: {}", topic, roomId, messageJson);

        kafkaTemplate.send(topic, String.valueOf(roomId), messageJson); //가운데가 key

    }
}

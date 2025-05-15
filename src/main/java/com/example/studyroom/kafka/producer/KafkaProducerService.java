package com.example.studyroom.kafka.producer;

import lombok.extern.slf4j.Slf4j;


public interface KafkaProducerService {

    void sendChatMessage(String topic, long roomId, String messageJson);
}

package com.example.studyroom.kafka.producer;

public interface KafkaProducerService {
    void sendChatMessage(String topic, String messageJson);
}

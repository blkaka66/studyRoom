package com.example.studyroom.service;

public interface KafkaProducerService {
    void sendChatMessage(String topic, String messageJson);
}

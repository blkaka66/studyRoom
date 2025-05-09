package com.example.studyroom.kafka.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DltMessageConsumer {

    @KafkaListener(topics = "chat-messages.DLT", groupId = "dlt-handler-group")
    public void handleDLT(String failedMessage) {
        log.warn("DLT 메시지 수신: {}", failedMessage);
        //여기는 메시지 포맷이깨지거나 다른 치명적인 이유로 시스템이 메시지를 못 보낸 버러지 메시지들 모임이기때문에
        //메시지를 복구하면안됨. 단순 개발자들이 확인하는곳
    }
}

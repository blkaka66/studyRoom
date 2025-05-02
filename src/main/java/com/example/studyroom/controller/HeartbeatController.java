package com.example.studyroom.controller;

import com.example.studyroom.dto.requestDto.HeartbeatRequestDto;
import com.example.studyroom.service.HeartbeatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller

public class HeartbeatController {

    private final HeartbeatService heartbeatService;

    public HeartbeatController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @MessageMapping("/heartbeat")
    public void receiveHeartbeat(HeartbeatRequestDto requestDto) {
        heartbeatService.handleHeartbeat(requestDto);
    }
}

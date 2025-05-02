package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.HeartbeatRequestDto;

public interface HeartbeatService {
    void handleHeartbeat(HeartbeatRequestDto requestDto);
}

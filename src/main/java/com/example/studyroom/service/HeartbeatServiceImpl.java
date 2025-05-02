package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.HeartbeatRequestDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HeartbeatServiceImpl implements HeartbeatService {


    @Override
    public void handleHeartbeat(HeartbeatRequestDto requestDto) {
        log.info("Heartbeat received from userId: {}, userType: {}, at: {}",
                requestDto.getUserId(), requestDto.getUserType(), requestDto.getTimestamp());
    }
}

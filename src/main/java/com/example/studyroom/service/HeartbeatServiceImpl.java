package com.example.studyroom.service;

import com.example.studyroom.dto.requestDto.HeartbeatRequestDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class HeartbeatServiceImpl implements HeartbeatService {

    private final StringRedisTemplate redisTemplate;

    public HeartbeatServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void handleHeartbeat(HeartbeatRequestDto requestDto) {

        Long userId = requestDto.getUserId();
        String userType = requestDto.getUserType();

        String key = "chat:heartbeat:" + userType + ":" + userId;
        // 하트비트 올때마다 Redis에 TTL 60초로 갱신
        redisTemplate.opsForValue().set(key, "alive", Duration.ofSeconds(60));

        log.info("하트비트 수신 및 Redis TTL 갱신 완료: {}", key);
        //근데 이걸로 할수있는게없다. 이미 subscribe를 만들어놔서.. 

    }
}

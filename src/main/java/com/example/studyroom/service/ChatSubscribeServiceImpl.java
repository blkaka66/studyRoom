package com.example.studyroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSubscribeServiceImpl implements ChatSubscribeService {

    private final StringRedisTemplate redisTemplate;

    private String makeKey(Long userId, Long roomId) {
        return "chat:subscribed:user:" + userId + ":room:" + roomId;
    }

    @Override
    public void subscribe(Long userId, Long roomId) {
        String key = makeKey(userId, roomId);
        redisTemplate.opsForValue().set(key, "true");
        log.info("구독 상태 저장됨 -> {}", key);

    }

    @Override
    public void unsubscribe(Long userId, Long roomId) {
        String key = makeKey(userId, roomId);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isSubscribed(Long userId, Long roomId) {
        String key = makeKey(userId, roomId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

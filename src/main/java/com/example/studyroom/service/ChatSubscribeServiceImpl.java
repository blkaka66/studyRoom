package com.example.studyroom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSubscribeServiceImpl implements ChatSubscribeService {

    private final StringRedisTemplate redisTemplate;

    private String makeKey(String userType, Long userId, Long roomId) {
        return "chat:subscribed:" + userType + ":" + userId + ":room:" + roomId;
    }

    @Override
    public void subscribe(String userType, Long userId, Long roomId) {
        String key = makeKey(userType, userId, roomId);
        redisTemplate.opsForValue().set(key, "true");
        log.info("구독 상태 저장됨 -> {}", key);
    }

    @Override
    public void unsubscribe(String userType, Long userId, Long roomId) {
        String key = makeKey(userType, userId, roomId);
        log.info("구독 삭제할 키 -> {}", key);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isSubscribed(String userType, Long userId, Long roomId) {
        String key = makeKey(userType, userId, roomId);
        log.info("isSubscribed-> {}", key);

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void unsubscribeAll(String userType, Long userId) {
        Set<String> keys = redisTemplate.keys("chat:subscribed:" + userType + ":" + userId + ":room:*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("구독 전체 해제 완료: {}", keys.size());
        }
    }
}

package com.example.studyroom.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
 //redis 이벤트리스너를 실행하기위해 필요함(ttl기능)
@Component
public class RedisKeyspaceConfig {

    private final StringRedisTemplate redisTemplate;

    public RedisKeyspaceConfig(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void enableKeyspaceNotifications() {
        redisTemplate.getConnectionFactory().getConnection().setConfig("notify-keyspace-events", "Ex");
    }
}

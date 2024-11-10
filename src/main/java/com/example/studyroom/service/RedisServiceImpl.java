package com.example.studyroom.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    // Constructor injection
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setValues(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public String getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setValuesWithTTL(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public Long getTTL(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}


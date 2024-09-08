package com.example.studyroom.service;

import java.time.Duration;

public interface RedisService {
    void setValues(String key, String value, Duration duration);
    String getValues(String key);
    void setValuesWithTTL(String key, String value, long ttlSeconds); //자리점유 만료시간체크
}

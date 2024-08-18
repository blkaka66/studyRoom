package com.example.studyroom.service;

import java.time.Duration;

public interface RedisService {
    void setValues(String key, String value, Duration duration);
    String getValues(String key);
}

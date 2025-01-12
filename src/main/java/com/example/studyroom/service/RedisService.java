package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.RemainTicketInfoResponseDto;

import java.time.Duration;

public interface RedisService {
    void setValues(String key, String value, Duration duration);
    String getValues(String key);
    void setValuesWithTTL(String key, String value, long ttlSeconds); //자리점유 만료시간체크
    Long getTTL(String key); //남은 ttl(수명)확인
    FinalResponseDto<String> deleteValue(String key);//key받아서 redis에 저장된거 삭제
    void checkAndNotifyExpiry(String redisKey, Long userId);
    String findMatchingKey(String pattern);
    boolean isKeyPresent(String key);

    String getUsingTicketCategoryInfoByUserId(Long userId);
    RemainTicketInfoResponseDto getReaminTimeInfoByUserId(String matchingKey, String ticketCategory);
}

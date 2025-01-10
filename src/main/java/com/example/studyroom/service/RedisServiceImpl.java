package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.dto.responseDto.RemainTicketInfoResponseDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.example.studyroom.type.ApiResult;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;
    private final PeriodTicketService periodTicketService;
    // Constructor injection
    public RedisServiceImpl(StringRedisTemplate redisTemplate , PeriodTicketService periodTicketService) {
        this.redisTemplate = redisTemplate;

        this.periodTicketService = periodTicketService;
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
    public FinalResponseDto<String> deleteValue(String key) {
        boolean isDeleted = redisTemplate.delete(key);  // 삭제 성공 여부를 확인

        if (isDeleted) {
            // 삭제가 성공하면 success 응답 반환
            return FinalResponseDto.success();
        } else {
            // 삭제 실패 시 failure 응답 반환
            return FinalResponseDto.failure(ApiResult.FAIL); // FAILURE는 적절한 오류 코드로 변경
        }
    }


    public void sendNotification(Long userId, String message) {
        // WebSocket을 통해 클라이언트에게 알림 발송
       // webSocketService.sendNotification(userId, message);
    }

    @Override
    // 1분 남았을 때 알림 발송
    public void checkAndNotifyExpiry(String redisKey, Long userId) {
        Long ttl = getTTL(redisKey);  // TTL 확인

        if (ttl != null && ttl <= 600) { // 1분 이하
            // 알림 발송
            sendNotification(userId, "티켓 만료시간 10분 남음");
        }
        if(ttl!= null && ttl <= 0) { //시간 만료되면 티켓삭제
            periodTicketService.removeExpiredTickets();
        }
    }

    @Override
    public String findMatchingKey(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null && !keys.isEmpty() ? keys.iterator().next() : null;
    }

    @Override
    // Redis에 키가 존재하는지 확인하는 메서드
    public boolean isKeyPresent(String key) {
        if (key == null || redisTemplate == null) {
            return false; // null이면 키가 없다고 처리
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));  // 키 존재 여부 확인
    }

    @Override
    public String getUsingTicketCategoryInfoByUserId(Long userId) {
        String pattern = "*:user:" + userId + ":*";
        return findMatchingKey(pattern);
    }

    @Override
    public RemainTicketInfoResponseDto getReaminTimeInfoByUserId(String matchingKey, String ticketCategory) {
        if (matchingKey != null) {
            String value = getValues(matchingKey);
            Long ttl = getTTL(matchingKey);

            return RemainTicketInfoResponseDto.builder()
                    .seatType(ticketCategory)
                    .key(matchingKey)
                    .value(value)
                    .ttl(ttl)
                    .build();
        }

        return null; // 매칭되는 키가 없는 경우
    }

}


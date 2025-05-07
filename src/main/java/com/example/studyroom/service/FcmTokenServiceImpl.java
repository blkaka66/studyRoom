package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.type.ApiResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FcmTokenServiceImpl implements FcmTokenService {
    private final StringRedisTemplate redisTemplate;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;

    public FcmTokenServiceImpl(StringRedisTemplate redisTemplate, ShopRepository shopRepository, MemberRepository memberRepository) {
        this.redisTemplate = redisTemplate;
        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;
    }

    private String makeKey(Long requesterId, String requesterType) {
        return "fcm:" + requesterType + ":" + requesterId;
    }

    @Override
    public FinalResponseDto<String> saveToken(Long requesterId, String requesterType, String token) {
        String key = makeKey(requesterId, requesterType);
        redisTemplate.opsForValue().set(key, token);
        log.info("Redis에 저장된 FCM 토큰: {} -> {}", key, token);
        return FinalResponseDto.success();
    }

    // 필요한 경우 알림 보낼 때 꺼내 쓸 수 있음
    public String getToken(Long requesterId, String requesterType) {
        String key = makeKey(requesterId, requesterType);
        return redisTemplate.opsForValue().get(key);
    }


}

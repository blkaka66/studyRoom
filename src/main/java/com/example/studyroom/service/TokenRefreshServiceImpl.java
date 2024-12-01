package com.example.studyroom.service;

import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.security.JwtUtil;
import com.example.studyroom.type.ApiResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshServiceImpl implements TokenRefreshService {
    private final JwtUtil jwtUtil;
    private final long accessTokenExpTime;
    private final RedisService redisService;

    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    public TokenRefreshServiceImpl(
                            JwtUtil jwtUtil,
                            @Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.accessToken_expiration_time}") long accessTokenExpTime,
                            @Value("${jwt.refreshToken_expiration_time}") long refreshTokenExpTime,
                            RedisService redisService,
                            StringRedisTemplate redisTemplate,
                            ShopRepository shopRepository,
                            MemberRepository memberRepository
    ) {

        this.jwtUtil = jwtUtil;
        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;

        this.redisService = redisService;
        this.accessTokenExpTime = accessTokenExpTime;

    }

    @Override
    public FinalResponseDto<String> handleExpiredAccessToken(String accessToken) {
        String token = accessToken.substring(7);

        System.out.println("역시 정상화는"+token);
        System.out.println("토큰!!!!!"+token);
        String role = jwtUtil.getRole(token);
        System.out.println("역할!!!!!");
        System.out.println("역할!!!!!"+role);

        String newAccessToken = null;
        String id = null;

        if (role.equals("SHOP")) {
            String email = jwtUtil.getShopEmail(token);
            ShopEntity shop = shopRepository.findByEmail(email);
            id = String.valueOf(shop.getId());
            Long ttl = redisService.getTTL(id);
            if (ttl > 0) {
                newAccessToken = jwtUtil.reCreateToken(shop, accessTokenExpTime);
                redisService.deleteValue(String.valueOf(shop.getId()));
                jwtUtil.createRefreshToken(shop);
                System.out.println("새로운 토큰!!"+newAccessToken);
                return FinalResponseDto.successWithData(newAccessToken);
            } else {
                System.out.println("토큰만료로 인한실패!!");
                return FinalResponseDto.failure(ApiResult.TOKEN_EXPIRED);
            }

        } else if (role.equals("CUSTOMER")) {
            Long userId = jwtUtil.getCustomerUserId(token);
            MemberEntity member = memberRepository.findById(userId).orElse(null);

            if (member != null) {
                id = String.valueOf(member.getId());
                Long ttl = redisService.getTTL(id);
                if (ttl > 0) {
                    newAccessToken = jwtUtil.reCreateToken(member, accessTokenExpTime);
                    redisService.deleteValue(String.valueOf(member.getId()));
                    jwtUtil.createRefreshToken(member);
                    System.out.println("새로운 토큰!!"+newAccessToken);
                    return FinalResponseDto.successWithData(newAccessToken);
                } else {
                    System.out.println("토큰만료로 인한실패!!");
                    return FinalResponseDto.failure(ApiResult.TOKEN_EXPIRED);
                }

            }
            return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
        }
        return FinalResponseDto.failure(ApiResult.DATA_NOT_FOUND);
    }
}

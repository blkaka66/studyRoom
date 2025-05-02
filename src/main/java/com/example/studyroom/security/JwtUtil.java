package com.example.studyroom.security;

import com.example.studyroom.dto.requestDto.MemberSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.service.RedisService;
import com.example.studyroom.type.ApiResult;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Member;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.model.MemberEntity;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@Slf4j
@Component
public class JwtUtil {


    private final Key key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final StringRedisTemplate redisTemplate;
    private final RedisService redisService;

    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;


    public JwtUtil(

            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.accessToken_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refreshToken_expiration_time}") long refreshTokenExpTime,
            RedisService redisService,
            StringRedisTemplate redisTemplate,
            ShopRepository shopRepository,
            MemberRepository memberRepository
    ) {

        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisService = redisService;
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.redisTemplate = redisTemplate;

    }

    /**
     * Access Token 생성
     *
     * @param shop
     * @return Access Token String
     */
    //ShopSignInRequestDto 또는 MemberSignInRequestDto 객체를 받아 액세스 토큰을 생성
    public String createAccessToken(ShopEntity shop) {
        return createToken(shop, accessTokenExpTime);
    }


    /**
     * Access Token 생성
     *
     * @param member
     * @return Access Token String
     */
    public String createAccessToken(MemberEntity member) {
        return createToken(member, accessTokenExpTime);
    }


    /**
     * JWT 생성
     *
     * @param shop
     * @param expireTime
     * @return JWT String
     */
    //ShopSignInRequestDto 또는 MemberSignInRequestDto에 기반하여 실제 JWT 토큰을 생성
    private String createToken(ShopEntity shop, long expireTime) {


        Claims claims = Jwts.claims();
        claims.put("email", shop.getEmail());
        claims.put("role", "SHOP");
        claims.put("shopId", shop.getId());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


    }

    public String reCreateToken(ShopEntity shop, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("email", shop.getEmail());
        claims.put("role", "SHOP");
        claims.put("shopId", shop.getId());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }


    /**
     * JWT 생성
     *
     * @param member
     * @param expireTime
     * @return JWT String
     */
    private String createToken(MemberEntity member, long expireTime) {
        //MemberEntity existingMember = memberRepository.findByPhoneAndPassword(member.getPhoneNumber(),member.getPassword());
        System.out.println("existingMember" + member.getName());
        Claims claims = Jwts.claims();
        claims.put("role", "CUSTOMER");
        claims.put("userId", member.getId());
        claims.put("shopId", member.getShop().getId());
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


    }

    public String reCreateToken(MemberEntity member, long expireTime) {


        Claims claims = Jwts.claims();
        claims.put("role", "CUSTOMER");
        claims.put("userId", member.getId());
        claims.put("shopId", member.getShop().getId());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public void createRefreshToken(MemberEntity member) {
        long id = member.getId();
        String refreshToken = createRefToken(refreshTokenExpTime);
        if (refreshToken != null) {
            String key = "refreshToken:member:" + id;
            redisService.setValuesWithTTL(key, refreshToken, refreshTokenExpTime);
        }
    }

    public void createRefreshToken(ShopEntity shop) {
        long id = shop.getId();
        String refreshToken = createRefToken(refreshTokenExpTime);
        if (refreshToken != null) {
            String key = "refreshToken:shop:" + id;
            redisService.setValuesWithTTL(key, refreshToken, refreshTokenExpTime);
        }
    }

    private String createRefToken(long expireTime) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);
        return Jwts.builder()
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


    }


    public enum TokenStatus { //토큰의 상태
        VALID, EXPIRED, INVALID
    }

    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */
    public TokenStatus validateToken(String token) {
        System.out.println("토큰!!!!!" + token);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return TokenStatus.VALID;  // 유효한 토큰
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            return TokenStatus.EXPIRED;  // 만료된 토큰
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return TokenStatus.INVALID;  // 그 외의 유효하지 않은 토큰
    }


    public void handleExpiredAccessToken(String token, HttpServletResponse response) throws IOException {
        String role = getRole(token);


        String newAccessToken = null;
        String id = null;

        if (role.equals("SHOP")) {
            String email = getShopEmail(token);
            ShopEntity shop = shopRepository.findByEmail(email);
            id = String.valueOf(shop.getId());
            Long ttl = redisService.getTTL(id);
            if (ttl > 0) {
                newAccessToken = reCreateToken(shop, accessTokenExpTime);
                redisService.deleteValue(String.valueOf(shop.getId()));
                createRefreshToken(shop);
            } else {
                //로그아웃처리
            }

        } else if (role.equals("CUSTOMER")) {
            Long userId = getCustomerUserId(token);
            MemberEntity member = memberRepository.findById(userId).orElse(null);

            if (member != null) {
                id = String.valueOf(member.getId());
                Long ttl = redisService.getTTL(id);
                if (ttl > 0) {
                    newAccessToken = reCreateToken(member, accessTokenExpTime);
                    redisService.deleteValue(String.valueOf(member.getId()));
                    createRefreshToken(member);
                } else {
                    //로그아웃처리
                }

            }

        }


    }

    /**
     * Token에서 Role 추출      // 1번
     *
     * @param token
     * @return Email Address
     */
    //토큰에서 사용자 역할(role)을 추출하여 반환
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Token에서 Email 추출
     *
     * @param token
     * @return Email Address
     */
    public String getShopEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /**
     * Token에서 PhoneNumber 추출
     *
     * @param token
     * @return Email Address
     */
    public String getCustomerPhoneNumber(String token) {
        return parseClaims(token).get("phoneNumber", String.class);
    }

    public Long getCustomerUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public Long getShopId(String token) {
        return parseClaims(token).get("shopId", Long.class);
    }


    public static MemberEntity getMember() {
        return (MemberEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static ShopEntity getShop() {
        return (ShopEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //토큰추출
    public String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
    }


    /**
     * JWT Claims 추출
     *
     * @param accessToken
     * @return JWT Claims
     */
    //JWT 토큰을 파싱하여 Claims 객체로 반환
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    //액세스토큰을 블랙리스트에 저장
    public void setAccessTokenWithRemainingTTL(String key, String accessToken) {
        // AccessToken의 만료 시간을 추출
        Claims claims = parseClaims(accessToken);
        Date expirationDate = claims.getExpiration();

        // 현재 시간과 만료 시간의 차이를 계산하여 남은 TTL을 구함
        long ttlInMillis = expirationDate.getTime() - System.currentTimeMillis();

        // TTL이 음수인 경우 (즉, 이미 만료된 경우)에는 TTL을 0으로 설정하여 삭제
        if (ttlInMillis > 0) {
            long ttlInSeconds = ttlInMillis / 1000; // 밀리초를 초 단위로 변환
            redisService.setValuesWithTTL(key, accessToken, ttlInSeconds);  // Redis에 남은 TTL을 적용하여 저장
        } else {
            // 이미 만료된 토큰 처리 (예: 삭제하거나 다른 조치 취하기)
            redisService.deleteValue(key);  // 예: 만료된 토큰 삭제
        }
    }


}

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
     * @param shop
     * @return Access Token String
     */
    //ShopSignInRequestDto 또는 MemberSignInRequestDto 객체를 받아 액세스 토큰을 생성
    public String createAccessToken(ShopSignInRequestDto shop) {
        return createToken(shop, accessTokenExpTime);
    }





    /**
     * Access Token 생성
     * @param member
     * @return Access Token String
     */
    public String createAccessToken(MemberSignInRequestDto member) {
        return createToken(member, accessTokenExpTime);
    }






    /**
     * JWT 생성
     * @param shop
     * @param expireTime
     * @return JWT String
     */
    //ShopSignInRequestDto 또는 MemberSignInRequestDto에 기반하여 실제 JWT 토큰을 생성
    private String createToken(ShopSignInRequestDto shop, long expireTime) {
        ShopEntity existingShop = shopRepository.findByEmailAndPassword(shop.getEmail(),shop.getPassword());
        if(existingShop != null) {
            Claims claims = Jwts.claims();
            claims.put("email", shop.getEmail());
            claims.put("role", "SHOP");
            claims.put("shopId", existingShop.getId());

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(Date.from(tokenValidity.toInstant()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }else{
            return null;//여기선 굳이 FinalResponseDto할필요 없겠지?
        }

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
     * @param member
     * @param expireTime
     * @return JWT String
     */
    private String createToken(MemberSignInRequestDto member, long expireTime) {
        MemberEntity existingMember = memberRepository.findByPhoneAndPassword(member.getPhoneNumber(),member.getPassword());
        if(existingMember != null) {
            Claims claims = Jwts.claims();
            //claims.put("phoneNumber", member.getPhoneNumber());전화번호는 개인정보니까 안넣어야하는거 아닌가?
            claims.put("role", "CUSTOMER");
            claims.put("userId",existingMember.getId());
            claims.put("shopId",existingMember.getShop().getId());

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(Date.from(tokenValidity.toInstant()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }else{
            return null;//여기선 굳이 FinalResponseDto할필요 없겠지?
        }


    }

    public String reCreateToken(MemberEntity member, long expireTime) {


        Claims claims = Jwts.claims();
        claims.put("role", "CUSTOMER");
        claims.put("userId",member.getId());
        claims.put("shopId",member.getShop().getId());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public void createRefreshToken(MemberEntity member) {
        long Id = member.getId();
        String refreshToken = createRefToken(refreshTokenExpTime);
        if (refreshToken != null) {
            String key = "refreshToken:" + Id;  // UUID로 key 생성
            redisService.setValuesWithTTL(key, refreshToken, refreshTokenExpTime);  // Redis에 저장
        }

    }

    public void createRefreshToken(ShopEntity shop) {
        long Id = shop.getId();
        String refreshToken = createRefToken(refreshTokenExpTime);
        if (refreshToken != null) {
            String key = "refreshToken:" + Id;  // UUID로 key 생성
            redisService.setValuesWithTTL(key, refreshToken, refreshTokenExpTime); // Redis에 저장
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
     * @param token
     * @return IsValidate
     */
    public TokenStatus validateToken(String token) {
        System.out.println("토큰!!!!!"+token);

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


    public void handleExpiredAccessToken(String token,HttpServletResponse response) throws IOException {
        String role = getRole(token);


        String newAccessToken = null;
        String id = null;

        if (role.equals("SHOP")) {
            String email = getShopEmail(token);
            ShopEntity shop = shopRepository.findByEmail(email);
            id = String.valueOf(shop.getId());
            Long ttl = redisService.getTTL(id);
            if(ttl>0){
                newAccessToken= reCreateToken(shop,accessTokenExpTime);
                redisService.deleteValue(String.valueOf(shop.getId()));
                createRefreshToken(shop);
            } else{
                //로그아웃처리
            }

        } else if (role.equals("CUSTOMER")) {
            Long userId = getCustomerUserId(token);
            MemberEntity member = memberRepository.findById(userId).orElse(null);

            if(member!=null){
                id = String.valueOf(member.getId());
                Long ttl = redisService.getTTL(id);
                if(ttl>0){
                    newAccessToken = reCreateToken(member,accessTokenExpTime);
                    redisService.deleteValue(String.valueOf(member.getId()));
                    createRefreshToken(member);
                }else{
                    //로그아웃처리
                }

            }

        }


    }

    /**
     * Token에서 Role 추출      // 1번
     * @param token
     * @return Email Address
     */
    //토큰에서 사용자 역할(role)을 추출하여 반환
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Token에서 Email 추출
     * @param token
     * @return Email Address
     */
    public String getShopEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /**
     * Token에서 PhoneNumber 추출
     * @param token
     * @return Email Address
     */
    public String getCustomerPhoneNumber(String token) {
        return parseClaims(token).get("phoneNumber", String.class);
    }

    public Long getCustomerUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * JWT Claims 추출
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



}

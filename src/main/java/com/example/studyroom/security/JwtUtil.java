package com.example.studyroom.security;
import com.example.studyroom.dto.requestDto.MemberSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignInRequestDto;
import com.example.studyroom.dto.requestDto.ShopSignUpRequestDto;
import com.example.studyroom.dto.responseDto.FinalResponseDto;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.type.ApiResult;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Member;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.model.MemberEntity;
@Slf4j
@Component
public class JwtUtil {


    private final Key key;
    private final long accessTokenExpTime;

    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;


    public JwtUtil(

            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime,
            ShopRepository shopRepository,
            MemberRepository memberRepository
    ) {

        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * Access Token 생성
     * @param shop
     * @return Access Token String
     */
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
                    .setIssuedAt(Date.from(now.toInstant()))
                    .setExpiration(Date.from(tokenValidity.toInstant()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }else{
            return null;//여기선 굳이 FinalResponseDto할필요 없겠지?
        }

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
                    .setIssuedAt(Date.from(now.toInstant()))
                    .setExpiration(Date.from(tokenValidity.toInstant()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }else{
            return null;//여기선 굳이 FinalResponseDto할필요 없겠지?
        }


    }

    /**
     * JWT 검증
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * Token에서 Role 추출      // 1번
     * @param token
     * @return Email Address
     */
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

    /**
     * JWT Claims 추출
     * @param accessToken
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}

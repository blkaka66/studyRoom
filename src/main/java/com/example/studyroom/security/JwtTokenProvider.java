package com.example.studyroom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey; // JWT 서명에 사용할 비밀키

    @Value("${jwt.expiration}")
    private long validityInMilliseconds; // 토큰 유효기간 (밀리초 단위)

    // JWT 토큰 생성
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username); // JWT의 클레임에 사용자 이름 설정
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // 토큰의 만료 시간 설정

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 발급 시간 설정
                .setExpiration(validity) // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘 및 비밀키 설정
                .compact(); // JWT 토큰 생성
    }

    // JWT 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // 서명에 사용할 비밀키 설정
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // 클레임에서 사용자 이름 추출
    }

    // JWT 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); // 토큰을 파싱하여 유효성을 검사합니다.
            return true;
        } catch (Exception e) {
            return false; // 토큰이 유효하지 않으면 false 반환
        }
    }

    // 요청에서 JWT 토큰 추출
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization"); // Authorization 헤더에서 토큰 추출
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 접두사 제거하고 토큰 반환
        }
        return null; // 토큰이 없으면 null 반환
    }
}

package com.example.studyroom.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class JwtCookieUtil {

    // 리프레시 토큰을 쿠키에 설정하고 응답에 추가하는 메서드
    public static void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response, long maxAgeInSeconds) {
        // 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        // 쿠키 속성 설정
        refreshTokenCookie.setHttpOnly(true);  // 클라이언트에서 접근 불가
        refreshTokenCookie.setSecure(false);  // HTTPS 연결에서만 전송
        refreshTokenCookie.setPath("/");  // 전체 경로에 대해 유효
        refreshTokenCookie.setMaxAge((int) maxAgeInSeconds);  // 쿠키의 유효 기간 설정 (초 단위)

        // 응답에 쿠키 추가
        response.addCookie(refreshTokenCookie);
    }

    // 쿠키에서 리프레시 토큰을 가져오는 메서드
    public static String getRefreshTokenFromCookies(jakarta.servlet.http.Cookie[] cookies) {
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // 리프레시 토큰을 삭제하는 메서드 (로그아웃 시 사용)
    public static void deleteRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);  // 쿠키 만료 처리
        response.addCookie(refreshTokenCookie);
    }
}

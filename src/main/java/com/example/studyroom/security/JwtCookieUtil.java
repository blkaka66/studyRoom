package com.example.studyroom.security;

import com.example.studyroom.dto.CookieDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class JwtCookieUtil {

    // id를 쿠키에 설정하고 응답에 추가하는 메서드
    public static void addInfoToCookie(String id, HttpServletResponse response, long maxAgeInSeconds) {
        // 쿠키 생성
        Cookie idCookie = new Cookie("id", id);

        // 쿠키 속성 설정
        idCookie.setHttpOnly(true);  // 클라이언트에서 접근 불가
        idCookie.setSecure(false);  // HTTPS 연결에서만 전송
        idCookie.setPath("/");  // 전체 경로에 대해 유효
        idCookie.setMaxAge((int) maxAgeInSeconds);  // 쿠키의 유효 기간 설정 (초 단위)
        System.out.println("idcookie"+idCookie);
        // 응답에 쿠키 추가

        response.addCookie(idCookie);
    }

    // 쿠키에서 id를 가져오는 메서드
    public static String getIdFromCookies(jakarta.servlet.http.Cookie[] cookies) {
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "id".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // id를 삭제하는 메서드 (로그아웃 시 사용)
    public static void deleteIdFromCookie(HttpServletResponse response) {
        Cookie idCookie = new Cookie("id", null);
        idCookie.setHttpOnly(true);
        idCookie.setSecure(false);
        idCookie.setPath("/");
        idCookie.setMaxAge(0);  // 쿠키 만료 처리
        response.addCookie(idCookie);
    }

    //쿠키에서 정보추출
    public static CookieDto getShopId(HttpServletRequest request) {
        CookieDto cookieDto = new CookieDto();  // CookieDto 객체 생성
        // 쿠키 배열 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // 쿠키 배열에서 'shopId' 쿠키 값을 찾기
            for (Cookie cookie : cookies) {
                if ("shopId".equals(cookie.getName())) {
                    cookieDto.setShopId(cookie.getValue());  // 'shopId' 값을 CookieDto에 설정
                }
            }
        }

        return cookieDto;  // CookieDto 객체 반환
    }
}

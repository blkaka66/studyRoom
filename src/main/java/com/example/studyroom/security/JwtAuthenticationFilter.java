package com.example.studyroom.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

// JWT 인증을 처리하기 위한 필터 클래스
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰을 관리하는 클래스
    private final CustomUserDetailsService userDetailsService; // 사용자 정보를 로드하는 서비스

    // 필터 생성자
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // 실제 필터링 로직을 처리하는 메서드

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청에서 JWT 토큰을 추출
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰이 유효한지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 사용자 이름을 추출
            String username = jwtTokenProvider.getUsername(token);

            // 사용자 정보를 로드
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // 인증 객체 생성
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // 인증 객체를 SecurityContextHolder에 설정
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}

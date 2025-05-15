package com.example.studyroom.config.Interceptor;

import com.example.studyroom.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
public class EmailRateLimitInterceptor implements HandlerInterceptor {
    private final RedisService redisService;
    private static final long MAX_REQUESTS = 1;
    private static final long TTL_SECONDS = 60;

    public EmailRateLimitInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr(); // 로컬 개발 환경 fallback
        } else {
            ip = ip.split(",")[0].trim(); // 여러 프록시 거쳤을 경우 첫 번째 IP가 진짜 ip
        }
        log.info("email send ip :{}", ip);
        String safeIp = ip.replace(":", "_");

        String key = "email:send:" + safeIp;

        if (!redisService.isKeyPresent(key)) {
            // Redis
            redisService.setValuesWithTTL(key, "1", TTL_SECONDS);
            return true;
        }

        long count = Long.parseLong(redisService.getValues(key)) + 1;

        if (count > MAX_REQUESTS) {
            log.info("email 요청 한도 초과");
            response.setStatus(429);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"message\":\"이메일 요청이 너무 많습니다. 잠시 후 다시 시도해주세요.\"}");
            return false; // Controller 호출 차단
        }

        long remainTime = redisService.getTTL(key);


        // Redis 에 해당 인증코드 인증 시간 설정
        redisService.setValuesWithTTL(key, String.valueOf(count), remainTime);
        return true; // 통과
    }
}

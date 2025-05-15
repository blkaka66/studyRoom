package com.example.studyroom.config;

import com.example.studyroom.config.Interceptor.EmailRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final EmailRateLimitInterceptor emailRateLimitInterceptor;

    public WebMvcConfig(EmailRateLimitInterceptor emailRateLimitInterceptor) {
        this.emailRateLimitInterceptor = emailRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(emailRateLimitInterceptor)
                .addPathPatterns("/email/send"); // 인터셉터 적용 경로
    }

}

package com.example.studyroom.security;

import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.security.CustomAccessDeniedHandler;
import com.example.studyroom.security.CustomAuthenticationEntryPoint;
import com.example.studyroom.security.JwtAuthFilter;
import com.example.studyroom.service.RedisService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.example.studyroom.security.JwtUtil;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig  {

    private final JwtUtil jwtUtil;
    private final CustomAccessDeniedHandler accessDeniedHandler;
//    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final RedisService redisService;
//    @Value("${cookie.secure.enabled}")
//    boolean cookieEnabled;

    private static final String[] AUTH_WHITELIST = {
            "/shop/login",
            "/member/login",
            "/shop/sign-in/shop-list",
            "/shop/sign-up",
            "/member/sign-up",
            "/shop/refreshToken"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        if(cookieEnabled) {
//            cookie.setSecure(true);
//        }

        //CSRF, CORS
        http.csrf((csrf) -> csrf.disable());
        //http.cors(Customizer.withDefaults());
        http.cors(cors -> cors.configurationSource(request -> {
            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
            corsConfig.setAllowedOrigins(List.of("https://studyroom-webfush.web.app", "http://localhost:5173", "http://localhost:5174", "http://localhost:3000","http://127.0.0.1:3000","http://127.0.0.1:5173")); // 여기에 허용할 출처를 명시
            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfig.setAllowedHeaders(List.of("*"));
            corsConfig.setAllowCredentials(true); // 쿠키 및 인증정보 허용
            return corsConfig;
        }));


        //세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        //FormLogin, BasicHttp 비활성화
        http.formLogin((form) -> form.disable());
        http.httpBasic(AbstractHttpConfigurer::disable);


        //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(new JwtAuthFilter(jwtUtil, memberRepository, shopRepository,redisService), UsernamePasswordAuthenticationFilter.class);


        http.exceptionHandling((exceptionHandling) -> exceptionHandling
//                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // 권한 규칙 작성
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers("/ws/**").permitAll()  // ✅ WebSocket 엔드포인트 인증 없이 허용
                        .requestMatchers("/chat/**").authenticated()  // ✅ 채팅 API는 인증 필요
                        .anyRequest().authenticated()
        );




        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}


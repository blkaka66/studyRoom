// TODO: 주석처리

//package com.example.studyroom.config;
//
//import com.example.studyroom.security.JwtAuthenticationFilter;
//import com.example.studyroom.security.JwtTokenProvider;
//import com.example.studyroom.security.CustomUserDetailsService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 관리 클래스
//    private final CustomUserDetailsService customUserDetailsService; // 사용자 정보 로드 서비스
//
//    // SecurityConfig 생성자: JwtTokenProvider와 CustomUserDetailsService를 주입받습니다.
//    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.customUserDetailsService = customUserDetailsService;
//    }
//
//    // HTTP 보안 설정
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable() // CSRF 보호를 비활성화합니다. JWT 기반 인증에서는 CSRF 공격이 필요하지 않으므로 비활성화합니다.
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않도록 설정합니다. JWT는 상태가 없기 때문에 서버에서 세션을 생성하지 않습니다.
//                .and()
//                .authorizeRequests()
//                .antMatchers("/auth/**").permitAll() // /auth/** 경로는 인증 없이 접근할 수 있습니다.
//                .anyRequest().authenticated() // 나머지 모든 요청은 인증이 필요합니다.
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
//                        UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
//    }
//
//    // AuthenticationManagerBuilder를 설정하여 사용자 인증 서비스와 비밀번호 인코더를 설정합니다.
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
//    }
//
//    // 비밀번호를 암호화하는 PasswordEncoder 빈을 생성합니다.
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(); // BCryptPasswordEncoder를 사용하여 비밀번호를 암호화합니다.
//    }
//}

package com.example.studyroom.security;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor

//OncePerRequestFilter를 상속받은 클래스로, 요청당 한 번만 실행됩니다.
// Spring Security에서 필터는 체인으로 연결되므로, 이 필터는 HTTP 요청이 들어올 때마다 실행되며,
// JWT 토큰을 검증하고 인증 정보를 설정하는 역할을 합니다.
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행 보장


    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final RedisService redisService;

    /**
     * JWT 토큰 검증 필터 수행
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // '/shop/refreshToken' 경로로 들어오는 요청은 만료검증을 제외(jwt토큰을 refresh하는 요청은 당연히 토큰이 만료돼있을테니까)
        String requestURI = request.getRequestURI();
        System.out.println("requestURI" + requestURI);
        if (requestURI.equals("/shop/refreshToken")) {
            System.out.println("필터건너뜀");
            UsernamePasswordAuthenticationToken authenticationToken = null;
            authenticationToken = new UsernamePasswordAuthenticationToken(new MemberEntity(), "", getAuthorities("CUSTOMER"));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        return "/shop/refreshToken".equals(requestURI);
    }


    @Transactional
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("리퀘스트 !!!!" + request);
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("헤더헤더 !!!!" + authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            System.out.println("필터토큰 !!!!" + token);
            JwtUtil.TokenStatus tokenStatus = jwtUtil.validateToken(token);

            if (tokenStatus == JwtUtil.TokenStatus.VALID) {
                if (isTokenBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    System.out.println("이 토큰은 현재 블랙리스트에 등록돼있습니다.");
                    response.getWriter().write("이 토큰은 현재 블랙리스트에 등록돼있습니다.");
                    return;
                }

                System.out.println("토큰이 유효합니다 !!!!");
                authenticateUser(token);

            } else if (tokenStatus == JwtUtil.TokenStatus.EXPIRED) { //재발급
                // jwtUtil.handleExpiredAccessToken(token, response);
                // return;  // 액세스 토큰 재발급 시 종료
                System.out.println("토큰이 만료됨 !!!!");
                SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.unauthenticated("", ""));

            }
        } else {
            System.out.println("넌뭐냐?");
            SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.unauthenticated("", ""));

        }
        filterChain.doFilter(request, response);  // 다음 필터로 넘기기
    }

    private boolean isTokenBlacklisted(String token) {
        String role = jwtUtil.getRole(token);
        String key;

        if ("SHOP".equals(role)) {
            Long shopId = jwtUtil.getShopId(token);
            key = "blacklist:accessToken:shop:" + shopId;
        } else if ("CUSTOMER".equals(role)) {
            Long userId = jwtUtil.getCustomerUserId(token);
            key = "blacklist:accessToken:member:" + userId;
        } else {
            return false;
        }

        if (redisService.isKeyPresent(key)) {
            return redisService.getValues(key).equals(token);
        }
        return false;
    }


    private void authenticateUser(String token) {
        System.out.println("authenticateUser!!!");
        String role = jwtUtil.getRole(token);
        UsernamePasswordAuthenticationToken authenticationToken = null;

        if (role.equals("SHOP")) {
            String email = jwtUtil.getShopEmail(token);
            ShopEntity shop = shopRepository.findByEmail(email);
            System.out.println("shop은" + shop);
            authenticationToken = new UsernamePasswordAuthenticationToken(shop, "", getAuthorities(role));

        } else if (role.equals("CUSTOMER")) {
            Long userId = jwtUtil.getCustomerUserId(token);
            MemberEntity member = memberRepository.findById(userId).orElse(null);
            if (member != null) {
                System.out.println("member" + member);
                authenticationToken = new UsernamePasswordAuthenticationToken(member, "", getAuthorities(role));
            }
        }
        if (authenticationToken != null) {
            System.out.println("토큰이 검증됨");
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + role);


        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

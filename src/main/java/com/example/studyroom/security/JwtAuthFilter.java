package com.example.studyroom.security;

import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNullApi;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    /**
     * JWT 토큰 검증 필터 수행
     */
//    @Override
//    //이 메서드는 요청이 들어올 때마다 실행되며, JWT 인증 로직을 수행합니다.
//    //request: HTTP 요청
//    //response: HTTP 응답
//    //filterChain: 요청을 계속해서 다음 필터로 넘기기 위해 사용합니다.
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        //HTTP 요청에서 Authorization 헤더를 가져옵니다. 이 헤더는 보통 Bearer <JWT> 형식으로 토큰을 포함하고 있습니다.
//        String authorizationHeader = request.getHeader("Authorization");
//
//
//        //JWT가 헤더에 있는 경우 Authorization 헤더가 Bearer로 시작하는지 확인합니다.
//        // 그렇다면, Bearer 이후의 JWT 토큰만 잘라서 token 변수에 저장합니다.
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String token = authorizationHeader.substring(7);
//            JwtUtil.TokenStatus tokenStatus = jwtUtil.validateToken(token);
//            //JWT 유효성 검증
//            if (tokenStatus == JwtUtil.TokenStatus.VALID) {
//                //jwtUtil.getRole(token)을 통해 토큰에서 사용자 역할(예: SHOP, CUSTOMER)을 추출합니다.
//                String role = jwtUtil.getRole(token);
//                //authenticationToken: 인증 객체로, UsernamePasswordAuthenticationToken은 사용자 이름과 권한 정보를 포함하는 객체입니다.
//                // 이 객체는 Spring Security에서 인증 정보를 설정할 때 사용됩니다.
//                UsernamePasswordAuthenticationToken authenticationToken = null;
//                UserDetails userDetails ;
//                if(role.equals("SHOP")){
//                    String email = jwtUtil.getShopEmail(token);
//                    ShopEntity shop =   shopRepository.findByEmail(email);
//                    authenticationToken = new UsernamePasswordAuthenticationToken(shop, "", getAuthorities(role));
//                }else if(role.equals("CUSTOMER") ){
//                    Long userId = jwtUtil.getCustomerUserId(token);
//                    MemberEntity member = memberRepository.findById(userId).orElse(null);
////                    String phoneNumber=jwtUtil.getCustomerPhoneNumber(token);
////                    MemberEntity member =   memberRepository.findByPhone(phoneNumber);
//                    if(member != null) {
//                        authenticationToken = new UsernamePasswordAuthenticationToken(member, "", getAuthorities(role));
//                    }
//                }else{
//                    return;
//                }
////인증이 성공적으로 처리되었다면, SecurityContextHolder에 인증 객체를 설정합니다.
//// 이렇게 설정된 인증 정보는 후속 요청에서 사용되며, Spring Security는 이를 통해 사용자가 인증되었는지 확인합니다.
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            } else if (tokenStatus == JwtUtil.TokenStatus.EXPIRED) { // 액세스토큰 만료된경우 리프래시토큰 통해 재발급
//
//            } else{
//                return;
//            }
//
//        }
//
//        filterChain.doFilter(request, response); // 다음 필터로 넘기기
//    }

    @Transactional
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            JwtUtil.TokenStatus tokenStatus = jwtUtil.validateToken(token);

            if (tokenStatus == JwtUtil.TokenStatus.VALID) {
                authenticateUser(token);
            } else if (tokenStatus == JwtUtil.TokenStatus.EXPIRED) { //재발급
                jwtUtil.handleExpiredAccessToken(token, response);
                return;  // 액세스 토큰 재발급 시 종료
            }
        }
        filterChain.doFilter(request, response);  // 다음 필터로 넘기기
    }

    private void authenticateUser(String token) {
        String role = jwtUtil.getRole(token);
        UsernamePasswordAuthenticationToken authenticationToken = null;

        if (role.equals("SHOP")) {
            String email = jwtUtil.getShopEmail(token);
            ShopEntity shop = shopRepository.findByEmail(email);
            authenticationToken = new UsernamePasswordAuthenticationToken(shop, "", getAuthorities(role));
        } else if (role.equals("CUSTOMER")) {
            Long userId = jwtUtil.getCustomerUserId(token);
            MemberEntity member = memberRepository.findById(userId).orElse(null);
            if (member != null) {
                authenticationToken = new UsernamePasswordAuthenticationToken(member, "", getAuthorities(role));
            }
        }
        if (authenticationToken != null) {
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

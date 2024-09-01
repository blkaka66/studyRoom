import com.example.studyroom.model.MemberEntity;
import com.example.studyroom.model.ShopEntity;
import com.example.studyroom.repository.MemberRepository;
import com.example.studyroom.repository.ShopRepository;
import com.example.studyroom.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor

public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행 보장

  //  private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private MemberRepository memberRepository;
    private ShopRepository shopRepository;
   // @Override
    /**
     * JWT 토큰 검증 필터 수행
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain,
                                    ShopRepository shopRepository,
                                    MemberRepository memberRepository
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        this.shopRepository = shopRepository;
        this.memberRepository = memberRepository;

        //JWT가 헤더에 있는 경우
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            //JWT 유효성 검증
            if (jwtUtil.validateToken(token)) {
                String role = jwtUtil.getRole(token);
                UsernamePasswordAuthenticationToken authenticationToken = null;
                UserDetails userDetails ;
                if(role.equals("SHOP")){
                    String Email=jwtUtil.getShopEmail(token);
                    ShopEntity shop =   shopRepository.findByEmail(Email);
                    authenticationToken = new UsernamePasswordAuthenticationToken(shop, null);
                }else if(role.equals("CUSTOMER") ){
                    String phoneNumber=jwtUtil.getCustomerPhoneNumber(token);
                    MemberEntity member =   memberRepository.findByPhone(phoneNumber);
                    authenticationToken = new UsernamePasswordAuthenticationToken(member, null);
                }else{
                    return;
                }
                
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);


                }
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 넘기기
    }
}

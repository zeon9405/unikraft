package com.unikraft.global.filter;

import com.unikraft.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 값 꺼내기
        String authorizationHeader = request.getHeader("Authorization");

        // 2. 토큰 존재 여부 및 형식 확인
        //    - Authorization 헤더가 없거나, "Bearer "로 시작하지 않으면 인증 실패로 간주하고 다음 필터로 넘깁니다.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 실제 토큰 추출
        //    - "Bearer " 접두사(7글자)를 제거하고 순수한 JWT 문자열만 남깁니다.
        String token = authorizationHeader.substring(7);

        // 4. 토큰 유효성 검증
        //    - JwtUtil을 사용하여 토큰이 위변조되지 않았는지, 만료되지 않았는지 확인합니다.
        if (jwtUtil.validateToken(token)) {

            // 5. 토큰에서 사용자 정보(이메일/ID) 추출
            String loginId = jwtUtil.getEmailFromToken(token);

            // 6. 인증 객체(Authentication) 생성
            //    - UsernamePasswordAuthenticationToken: 스프링 시큐리티가 사용하는 표준 인증 객체입니다.
            //    - 첫 번째 인자: 사용자 식별자 (Principal) -> 여기서는 loginId
            //    - 두 번째 인자: 비밀번호 (Credentials) -> 이미 인증되었으므로 null
            //    - 세 번째 인자: 권한 목록 (Authorities) -> 지금은 권한이 없으므로 빈 리스트
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginId, null, new ArrayList<>());

            // 7. 인증 객체에 추가 정보 설정 (선택 사항)
            //    - 요청 정보(IP 주소, 세션 ID 등)를 인증 객체에 포함시킵니다.
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 8. SecurityContextHolder에 인증 객체 저장 (핵심!)
            //    - 이제 스프링 시큐리티는 "이 요청은 인증된 사용자가 보낸 것"이라고 인식하게 됩니다.
            //    - 이후의 컨트롤러 등에서 @AuthenticationPrincipal 등을 통해 사용자 정보를 꺼내 쓸 수 있습니다.
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 9. 다음 필터로 진행
        //    - 인증 처리가 끝났으므로, 다음 필터(또는 컨트롤러)에게 요청을 넘깁니다.
        filterChain.doFilter(request, response);
    }
}
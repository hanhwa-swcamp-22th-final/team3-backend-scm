package com.ohgiraffers.team3backendscm.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP 요청마다 Authorization 헤더에서 JWT 를 추출해 인증 정보를 SecurityContext 에 등록하는 필터.
 *
 * <p>처리 흐름 (Admin 모듈의 JwtAuthenticationFilter 와 동일한 패턴):
 * <ol>
 *   <li>Authorization: Bearer {token} 헤더에서 토큰 추출</li>
 *   <li>{@link JwtTokenProvider}로 서명·만료 검증</li>
 *   <li>JWT sub(employeeCode)로 {@link UserDetailsService#loadUserByUsername} 호출 → DB 조회</li>
 *   <li>반환된 {@code CustomUserDetails}(employeeId 포함)를 SecurityContext 에 저장</li>
 * </ol>
 * 토큰이 없거나 유효하지 않으면 SecurityContext 를 변경하지 않고 다음 필터로 넘긴다.
 * </p>
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검사
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 3. JWT sub(사원코드)로 DB에서 사용자 정보 로드 → CustomUserDetails(employeeId 포함)
            String employeeCode = jwtTokenProvider.getEmployeeCode(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(employeeCode);

            // 4. Authentication 객체 생성 후 SecurityContext에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

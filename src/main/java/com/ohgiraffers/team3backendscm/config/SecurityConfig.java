package com.ohgiraffers.team3backendscm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스.
 * 본 서버는 비브라우저 클라이언트(Admin 서버 등)가 호출하는 REST API 전용 서버이다.
 * 쿠키 기반 인증을 사용하지 않으므로 CSRF 보호가 불필요하다.
 * 운영 환경 전환 시 JWT 필터 추가, 역할 기반 접근 제어(RBAC) 설정이 필요하다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTP 보안 필터 체인을 구성한다.
     * - CSRF: 비활성화 — 비브라우저 REST API 서버이므로 쿠키 기반 CSRF 공격 벡터 없음
     * - 세션: STATELESS — 서버 측 세션을 생성하지 않음
     * - 인가: 모든 요청 허용 (개발 단계)
     *
     * @param http HttpSecurity 설정 빌더
     * @return 구성된 SecurityFilterChain Bean
     * @throws Exception 설정 처리 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // lgtm[java/spring-disabled-csrf-protection]
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}

package com.ohgiraffers.team3backendscm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스.
 * 현재는 개발 편의를 위해 CSRF 보호를 비활성화하고, 모든 요청을 인증 없이 허용한다.
 * 운영 환경 전환 시 JWT 필터 추가, 역할 기반 접근 제어(RBAC) 설정이 필요하다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTP 보안 필터 체인을 구성한다.
     * - CSRF: 비활성화 (REST API 서버이므로)
     * - 인가: 모든 요청 허용 (개발 단계)
     *
     * @param http HttpSecurity 설정 빌더
     * @return 구성된 SecurityFilterChain Bean
     * @throws Exception 설정 처리 중 예외 발생 시
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}

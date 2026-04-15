package com.ohgiraffers.team3backendscm.infrastructure.client.feign;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Admin Feign Client 설정.
 * 현재 요청의 Authorization 헤더(Bearer JWT)를 Admin 서비스 호출 시 그대로 전달한다.
 */
@Configuration
public class AdminFeignConfiguration {

    @Bean
    public RequestInterceptor adminAuthorizationForwardingInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null && !authorization.isBlank()) {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, authorization);
            }
        };
    }
}

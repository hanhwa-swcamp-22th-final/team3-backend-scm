package com.ohgiraffers.team3backendscm.config.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA Auditing에서 현재 작업 수행자(Auditor)를 제공하는 구현체.
 * SecurityContext에서 인증 정보를 꺼내 CustomUserDetails의 employee_id를 반환한다.
 * 이 값이 엔티티의 {@code @CreatedBy} / {@code @LastModifiedBy} 필드에 자동으로 기록된다.
 * JpaAuditingConfig에서 "auditorProvider"라는 Bean 이름으로 참조된다.
 */
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

    /**
     * 현재 인증된 사용자의 employee_id를 반환한다.
     * 인증 정보가 없거나 principal이 CustomUserDetails가 아닌 경우 빈 Optional을 반환한다.
     *
     * @return 현재 사용자의 employee_id, 인증 정보 없으면 Optional.empty()
     */
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return Optional.of(customUserDetails.getEmployeeId());
        }

        return Optional.empty();
    }
}

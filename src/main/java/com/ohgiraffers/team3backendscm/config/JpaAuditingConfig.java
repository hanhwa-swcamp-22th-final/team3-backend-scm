package com.ohgiraffers.team3backendscm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화 설정 클래스.
 * {@code @EnableJpaAuditing} 을 통해 엔티티의 {@code @CreatedDate}, {@code @LastModifiedDate},
 * {@code @CreatedBy}, {@code @LastModifiedBy} 어노테이션이 자동으로 채워지도록 한다.
 * auditorAwareRef 로 "auditorProvider" Bean(AuditorAwareImpl)을 지정하여
 * 현재 로그인한 사용자의 employee_id 를 created_by / updated_by 에 기록한다.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
}

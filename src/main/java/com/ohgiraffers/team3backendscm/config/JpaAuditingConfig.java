package com.ohgiraffers.team3backendscm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing ?œì„±???¤ì • ?´ë˜??
 * {@code @EnableJpaAuditing} ???µí•´ ?”í‹°?°ì˜ {@code @CreatedDate}, {@code @LastModifiedDate},
 * {@code @CreatedBy}, {@code @LastModifiedBy} ?´ë…¸?Œì´?˜ì´ ?ë™?¼ë¡œ ì±„ì›Œì§€?„ë¡ ?œë‹¤.
 * auditorAwareRef ë¡?"auditorProvider" Bean(AuditorAwareImpl)??ì§€?•í•˜??
 * ?„ì¬ ë¡œê·¸?¸í•œ ?¬ìš©?ì˜ employee_id ë¥?created_by / updated_by ??ê¸°ë¡?œë‹¤.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {
}

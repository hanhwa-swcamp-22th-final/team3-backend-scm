package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository.JpaProductRepository;

/**
 * Product에 대한 도메인 리포지토리 인터페이스.
 * JPA 구현체는 {@link JpaProductRepository}에 위임한다.
 */
public interface ProductRepository extends JpaProductRepository {
}

package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Product 도메인 엔티티의 핵심 비즈니스 규칙을 검증하는 단위 테스트.
 *
 * <p>테스트 전략: 순수 단위 테스트 (외부 의존성 없음)
 * - create() 팩토리 메서드로 생성 시 필드 초기화 검증
 * - update() 호출 시 제품명·코드 변경 검증
 * </p>
 */
class ProductTest {

    @Test
    @DisplayName("제품 생성: create() 팩토리 메서드로 생성 시 모든 필드가 올바르게 초기화된다")
    void create_Success() {
        // when
        Product product = Product.create(1L, "테스트 제품", "TEST-001");

        // then
        assertEquals(1L, product.getProductId());
        assertEquals("테스트 제품", product.getProductName());
        assertEquals("TEST-001", product.getProductCode());
    }

    @Test
    @DisplayName("제품 수정: update() 호출 시 제품명과 코드가 변경된다")
    void update_Success() {
        // given
        Product product = Product.create(1L, "기존 제품명", "OLD-001");

        // when
        product.update("변경 제품명", "NEW-001");

        // then
        assertEquals("변경 제품명", product.getProductName());
        assertEquals("NEW-001", product.getProductCode());
    }
}

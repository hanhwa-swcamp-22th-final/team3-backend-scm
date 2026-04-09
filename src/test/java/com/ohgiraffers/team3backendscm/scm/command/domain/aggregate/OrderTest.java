package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Order 도메인 엔티티의 핵심 비즈니스 규칙을 검증하는 단위 테스트.
 *
 * <p>테스트 전략: 순수 단위 테스트 (외부 의존성 없음)
 * - 주문 생성 시 초기 상태 검증
 * - 주문 상태 전이(REGISTERED → ANALYZED) 검증
 * - 잘못된 상태에서 기술자 배정 시 예외 발생 검증
 * - INPROGRESS → COMPLETED 전이(complete) 검증
 * - INPROGRESS → ANALYZED 롤백(cancelAssignment) 검증
 * - 납기 기준 긴급 주문 판별 로직 검증
 * </p>
 */
class OrderTest {

    // 시간 기반 고유 ID를 생성하는 유틸리티
    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    @Test
    @DisplayName("주문 생성 시 초기 상태는 REGISTERED다")
    void createOrder_InitialStatusIsRegistered() {
        // given - 납기 10일 후 주문 객체 생성
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(10));

        // then - 생성 직후 상태는 반드시 REGISTERED 이어야 한다
        assertEquals(OrderStatus.REGISTERED, order.getStatus());
    }

    @Test
    @DisplayName("REGISTERED → ANALYZED 상태 전이가 가능하다")
    void moveToQueued_Success() {
        // given - REGISTERED 상태의 주문 생성
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(10));

        // when - OCSA 분석 완료 후 ANALYZED 상태로 전이
        order.moveToQueued();

        // then - 상태가 ANALYZED로 변경되었는지 확인
        assertEquals(OrderStatus.ANALYZED, order.getStatus());
    }

    @Test
    @DisplayName("ANALYZED 상태가 아닌 주문에 기술자 배정 시 예외가 발생한다")
    void assignTechnician_FailWhenNotAnalyzed() {
        // given - 초기(REGISTERED) 상태의 주문 생성
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(10));

        // when & then - ANALYZED 상태가 아닌 주문에 배정 시도 시 IllegalStateException 발생 확인
        assertThrows(IllegalStateException.class, () -> order.assignTechnician(1L));
    }

    @Test
    @DisplayName("INPROGRESS 상태의 주문은 COMPLETED로 완료 처리된다")
    void complete_Success() {
        // given - INPROGRESS 상태의 주문 생성
        Order order = new Order(idGenerator.generate(), "ORD-0302", OrderStatus.INPROGRESS, LocalDate.now().plusDays(5));

        // when - 작업 완료 처리
        order.complete();

        // then - 상태가 COMPLETED로 전환되어야 한다
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    @DisplayName("INPROGRESS 상태가 아닌 주문을 완료 처리하면 예외가 발생한다")
    void complete_FailWhenNotInprogress() {
        // given - ANALYZED 상태의 주문 (INPROGRESS 아님)
        Order order = new Order(idGenerator.generate(), "ORD-0302", OrderStatus.ANALYZED, LocalDate.now().plusDays(5));

        // when & then - INPROGRESS 아닌 상태에서 complete() 호출 시 IllegalStateException 발생
        assertThrows(IllegalStateException.class, order::complete);
    }

    @Test
    @DisplayName("INPROGRESS 주문의 배정을 취소하면 ANALYZED로 롤백된다")
    void cancelAssignment_RollbackToAnalyzed() {
        // given - INPROGRESS 상태의 주문 생성
        Order order = new Order(idGenerator.generate(), "ORD-0303", OrderStatus.INPROGRESS, LocalDate.now().plusDays(5));

        // when - 배정 취소
        order.cancelAssignment();

        // then - 상태가 ANALYZED로 롤백되어야 한다
        assertEquals(OrderStatus.ANALYZED, order.getStatus());
    }

    @Test
    @DisplayName("INPROGRESS 상태가 아닌 주문의 배정 취소 시 예외가 발생한다")
    void cancelAssignment_FailWhenNotInprogress() {
        // given - ANALYZED 상태의 주문 (배정 취소 불가)
        Order order = new Order(idGenerator.generate(), "ORD-0303", OrderStatus.ANALYZED, LocalDate.now().plusDays(5));

        // when & then - INPROGRESS 아닌 상태에서 cancelAssignment() 호출 시 IllegalStateException 발생
        assertThrows(IllegalStateException.class, order::cancelAssignment);
    }

    @Test
    @DisplayName("납기가 3일 이내인 주문은 긴급 주문이다")
    void isUrgent_WhenDeadlineWithin3Days() {
        // given - 납기 2일 후 주문 생성 (긴급 기준: 3일 이내)
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(2));

        // then - 긴급 주문으로 판별되어야 한다
        assertTrue(order.isUrgent());
    }
    @Test
    @DisplayName("난이도 분석 결과를 반영하면 주문 상태가 ANALYZED로 변경된다")
    void applyDifficultyAnalysis_Success() {
        Order order = Order.register(idGenerator.generate(), 1L, 2L, "ORD-0501", 40, LocalDate.now().plusDays(7), true);

        order.applyDifficultyAnalysis(
            new BigDecimal("7.20"),
            new BigDecimal("8.10"),
            new BigDecimal("6.40"),
            new BigDecimal("9.00"),
            new BigDecimal("10.00"),
            new BigDecimal("88.50"),
            DifficultyGrade.D4
        );

        assertEquals(OrderStatus.ANALYZED, order.getStatus());
        assertEquals(DifficultyGrade.D4, order.getDifficultyGrade());
        assertEquals(0, order.getDifficultyScore().compareTo(new BigDecimal("88.50")));
    }

    @Test
    @DisplayName("진행 중인 주문에는 난이도 분석 결과를 다시 반영할 수 없다")
    void applyDifficultyAnalysis_FailWhenInProgress() {
        Order order = new Order(idGenerator.generate(), "ORD-0502", OrderStatus.INPROGRESS, LocalDate.now().plusDays(3));

        assertThrows(IllegalStateException.class, () -> order.applyDifficultyAnalysis(
            new BigDecimal("7.20"),
            new BigDecimal("8.10"),
            new BigDecimal("6.40"),
            new BigDecimal("9.00"),
            new BigDecimal("10.00"),
            new BigDecimal("88.50"),
            DifficultyGrade.D4
        ));
    }
}

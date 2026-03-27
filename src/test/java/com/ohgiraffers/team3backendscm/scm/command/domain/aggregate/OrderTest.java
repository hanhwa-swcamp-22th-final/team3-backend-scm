package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    @Test
    @DisplayName("주문 생성 시 초기 상태는 REGISTERED다")
    void createOrder_InitialStatusIsRegistered() {
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(10));

        assertEquals(OrderStatus.REGISTERED, order.getStatus());
    }

    @Test
    @DisplayName("REGISTERED → ANALYZED 상태 전이가 가능하다")
    void moveToQueued_Success() {
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(10));
        order.moveToQueued();

        assertEquals(OrderStatus.ANALYZED, order.getStatus());
    }

    @Test
    @DisplayName("ANALYZED 상태가 아닌 주문에 기술자 배정 시 예외가 발생한다")
    void assignTechnician_FailWhenNotAnalyzed() {
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(10));

        assertThrows(IllegalStateException.class, () -> order.assignTechnician(1L));
    }

    @Test
    @DisplayName("납기가 3일 이내인 주문은 긴급 주문이다")
    void isUrgent_WhenDeadlineWithin3Days() {
        Order order = new Order(idGenerator.generate(), "ORD-0301", "Ti-6Al-4V 항공부품", LocalDate.now().plusDays(2));

        assertTrue(order.isUrgent());
    }
}

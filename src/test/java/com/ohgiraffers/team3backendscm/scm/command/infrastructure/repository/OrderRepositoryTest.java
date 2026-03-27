package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("ANALYZED 상태 주문만 조회된다")
    void findByStatus_OnlyAnalyzed() {
        List<Order> result = orderRepository.findByStatus(OrderStatus.ANALYZED);

        assertTrue(result.stream().allMatch(o -> o.getStatus() == OrderStatus.ANALYZED));
    }

    @Test
    @DisplayName("납기 N일 이내 주문이 긴급 주문으로 조회된다")
    void findUrgentOrders_WithinDeadline() {
        List<Order> result = orderRepository.findUrgentOrders(LocalDate.now().plusDays(3));

        assertTrue(result.stream().allMatch(Order::isUrgent));
    }
}

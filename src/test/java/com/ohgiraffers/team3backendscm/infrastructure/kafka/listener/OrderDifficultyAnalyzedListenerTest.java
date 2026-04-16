package com.ohgiraffers.team3backendscm.infrastructure.kafka.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderDifficultyAnalyzedEvent;
import com.ohgiraffers.team3backendscm.scm.command.application.service.OrderDifficultySnapshotCommandService;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderDifficultyAnalyzedListenerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDifficultySnapshotCommandService orderDifficultySnapshotCommandService;

    @InjectMocks
    private OrderDifficultyAnalyzedListener orderDifficultyAnalyzedListener;

    @Test
    @DisplayName("Applies analyzed event to registered order and saves it")
    void listen_AppliesDifficultyResult() {
        Order order = Order.register(1L, 10L, 20L, "ORD-101", 50, LocalDate.now().plusDays(5), true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderDifficultyAnalyzedListener.listen(new OrderDifficultyAnalyzedEvent(
            1L,
            new BigDecimal("7.20"),
            new BigDecimal("8.10"),
            new BigDecimal("6.40"),
            new BigDecimal("9.00"),
            new BigDecimal("10.00"),
            new BigDecimal("88.50"),
            "D4",
            "ANALYZED",
            LocalDateTime.now()
        ));

        verify(orderRepository).save(order);
        verify(orderDifficultySnapshotCommandService).publishSnapshotAfterCommit(order);
    }

    @Test
    @DisplayName("Skips saving when order is already in progress")
    void listen_SkipsWhenOrderAlreadyInProgress() {
        Order order = new Order(2L, "ORD-102", OrderStatus.INPROGRESS, LocalDate.now().plusDays(3));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        orderDifficultyAnalyzedListener.listen(new OrderDifficultyAnalyzedEvent(
            2L,
            new BigDecimal("7.20"),
            new BigDecimal("8.10"),
            new BigDecimal("6.40"),
            new BigDecimal("9.00"),
            new BigDecimal("10.00"),
            new BigDecimal("88.50"),
            "D4",
            "ANALYZED",
            LocalDateTime.now()
        ));

        verify(orderRepository, never()).save(any());
    }
}

package com.ohgiraffers.team3backendscm.scm.command.application.service.admin;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher.OrderEventPublisher;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OcsaWeightConfig;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OcsaWeightConfigRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OcsaWeightConfigRepository ocsaWeightConfigRepository;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderCommandService orderCommandService;

    @Test
    @DisplayName("주문 등록 성공: 새로운 주문이 생성되고 repository.save()가 호출된다")
    void createOrder_Success() {
        // given
        OrderCreateRequest request = new OrderCreateRequest(1L, 2L, "ORD-123", 100, LocalDate.now().plusDays(5), false);
        Product product = Product.create(1L, "PRODUCT-A", "CODE-A");
        OcsaWeightConfig weightConfig = org.mockito.Mockito.mock(OcsaWeightConfig.class);
        given(idGenerator.generate()).willReturn(10L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));
        given(ocsaWeightConfigRepository.findById(2L)).willReturn(Optional.of(weightConfig));

        // when
        Long id = orderCommandService.create(request);

        // then
        assertEquals(10L, id);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventPublisher, times(1)).publishRegistered(any());
    }

    @Test
    @DisplayName("주문 수정 성공: REGISTERED 상태의 주문 정보를 수정한다")
    void updateOrder_Success() {
        // given
        Order order = Order.register(1L, 2L, 3L, "ORD-OLD", 10, LocalDate.now().plusDays(5), true);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        OrderUpdateRequest request = new OrderUpdateRequest(20L, "ORD-NEW", 50, LocalDate.now().plusDays(10));

        // when
        orderCommandService.update(1L, request);

        // then
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("주문 수정 실패: REGISTERED 상태가 아니면 예외를 던진다")
    void updateOrder_Fail_WhenNotRegistered() {
        // given
        Order order = Order.register(1L, 2L, 3L, "ORD-OLD", 10, LocalDate.now().plusDays(5), true);
        order.moveToQueued();
        order.assignTechnician(100L); // INPROGRESS 상태로 변경

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        OrderUpdateRequest request = new OrderUpdateRequest(20L, "ORD-NEW", 50, LocalDate.now().plusDays(10));

        // when & then
        assertThrows(IllegalStateException.class, () -> orderCommandService.update(1L, request));
    }

    @Test
    @DisplayName("주문 삭제 성공: REGISTERED 상태의 주문을 삭제한다")
    void deleteOrder_Success() {
        // given
        Order order = Order.register(1L, 2L, 3L, "ORD-OLD", 10, LocalDate.now().plusDays(5), true);
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when
        orderCommandService.delete(1L);

        // then
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    @DisplayName("주문 삭제 실패: REGISTERED 상태가 아니면 예외를 던진다")
    void deleteOrder_Fail_WhenNotRegistered() {
        // given
        Order order = Order.register(1L, 2L, 3L, "ORD-OLD", 10, LocalDate.now().plusDays(5), true);
        order.moveToQueued();
        order.assignTechnician(100L); // 상태 변경

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        // when & then
        assertThrows(IllegalStateException.class, () -> orderCommandService.delete(1L));
    }
}

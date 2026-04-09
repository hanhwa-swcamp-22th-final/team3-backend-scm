package com.ohgiraffers.team3backendscm.scm.command.application.service.admin;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderRegisteredEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher.OrderEventPublisher;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.ProductRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Admin 모듈에서 호출하는 주문 등록, 수정, 삭제용 Command 서비스이다.
 */
@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private static final BigDecimal DEFAULT_WEIGHT = new BigDecimal("0.25");
    private static final BigDecimal DEFAULT_ALPHA_WEIGHT = new BigDecimal("0.50");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;
    private final OrderEventPublisher orderEventPublisher;

    /**
     * 주문을 등록한다.
     *
     * @param request 주문 등록 요청 DTO
     * @return 생성된 주문 ID
     */
    @Transactional
    public Long create(OrderCreateRequest request) {
        Long id = idGenerator.generate();
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id=" + request.getProductId()));

        Order order = Order.register(
                id,
                request.getProductId(),
                request.getConfigId(),
                request.getOrderNumber(),
                request.getOrderQuantity(),
                request.getDueDate(),
                request.getProcessStepCount(),
                request.getToleranceMm(),
                request.getSkillLevel(),
                request.getIsFirstOrder()
        );
        orderRepository.save(order);
        publishRegisteredAfterCommit(new OrderRegisteredEvent(
                id,
                request.getProductId(),
                request.getConfigId(),
                request.getOrderNumber(),
                request.getOrderQuantity(),
                request.getProcessStepCount(),
                request.getToleranceMm(),
                request.getSkillLevel(),
                request.getDueDate(),
                request.getIsFirstOrder(),
                product.getProductName(),
                product.getProductCode(),
                null,
                DEFAULT_WEIGHT,
                DEFAULT_WEIGHT,
                DEFAULT_WEIGHT,
                DEFAULT_WEIGHT,
                DEFAULT_ALPHA_WEIGHT,
                LocalDateTime.now()
        ));
        return id;
    }

    /**
     * 주문 기본 정보를 수정한다.
     *
     * @param orderId 수정할 주문 ID
     * @param request 주문 수정 요청 DTO
     */
    @Transactional
    public void update(Long orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + orderId));
        order.updateInfo(request.getProductId(), request.getOrderNumber(),
                request.getOrderQuantity(), request.getDueDate(),
                request.getProcessStepCount(), request.getToleranceMm(), request.getSkillLevel());
        orderRepository.save(order);
    }

    /**
     * 주문을 삭제한다.
     *
     * @param orderId 삭제할 주문 ID
     */
    @Transactional
    public void delete(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + orderId));
        if (order.getStatus() != OrderStatus.REGISTERED) {
            throw new IllegalStateException("REGISTERED 상태의 주문만 삭제할 수 있습니다. 현재 상태: " + order.getStatus());
        }
        orderRepository.delete(order);
    }

    private void publishRegisteredAfterCommit(OrderRegisteredEvent event) {
        Runnable publishAction = () -> orderEventPublisher.publishRegistered(event);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishAction.run();
                }
            });
            return;
        }

        publishAction.run();
    }
}

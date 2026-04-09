package com.ohgiraffers.team3backendscm.scm.command.application.service.admin;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderRegisteredEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher.OrderEventPublisher;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OcsaWeightConfig;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Product;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OcsaWeightConfigRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Admin???몄텧?섎뒗 二쇰Ц(Order) ?깅줉쨌?섏젙쨌??젣 Command ?쒕퉬??
 * <p>
 * - ?깅줉: REGISTERED ?곹깭濡??앹꽦 (OCSA 遺꾩꽍? SCM???댄썑 泥섎━)
 * - ?섏젙: REGISTERED ?곹깭??二쇰Ц留??덉슜 (SCM ?뚰겕?뚮줈??吏꾩엯 ?꾧퉴吏留?蹂寃?媛??
 * - ??젣: REGISTERED ?곹깭??二쇰Ц留??덉슜
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OcsaWeightConfigRepository ocsaWeightConfigRepository;
    private final IdGenerator idGenerator;
    private final OrderEventPublisher orderEventPublisher;

    /**
     * 二쇰Ц???깅줉?쒕떎. 珥덇린 ?곹깭??REGISTERED濡?怨좎젙?쒕떎.
     *
     * @param request 二쇰Ц ?뺣낫瑜??댁? ?붿껌 DTO
     * @return ?앹꽦??二쇰Ц ID
     */
    @Transactional
    public Long create(OrderCreateRequest request) {
        Long id = idGenerator.generate();
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new NoSuchElementException("?곹뭹??李얠쓣 ???놁뒿?덈떎. id=" + request.getProductId()));
        OcsaWeightConfig weightConfig = ocsaWeightConfigRepository.findById(request.getConfigId())
            .orElseThrow(() -> new NoSuchElementException("OCSA ?ㅼ젙??李얠쓣 ???놁뒿?덈떎. id=" + request.getConfigId()));

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
                weightConfig.getIndustryPreset() == null ? null : weightConfig.getIndustryPreset().name(),
                weightConfig.getWeightV1(),
                weightConfig.getWeightV2(),
                weightConfig.getWeightV3(),
                weightConfig.getWeightV4(),
                weightConfig.getAlphaWeight(),
                LocalDateTime.now()
        ));
        return id;
    }

    /**
     * 二쇰Ц 湲곕낯 ?뺣낫瑜??섏젙?쒕떎. REGISTERED ?곹깭??二쇰Ц留??덉슜?쒕떎.
     *
     * @param orderId ?섏젙??二쇰Ц ID
     * @param request 蹂寃쏀븷 ?뺣낫瑜??댁? ?붿껌 DTO
     * @throws NoSuchElementException 二쇰Ц??李얠쓣 ???놁쓣 寃쎌슦
     * @throws IllegalStateException  REGISTERED ?곹깭媛 ?꾨땺 寃쎌슦
     */
    @Transactional
    public void update(Long orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("二쇰Ц??李얠쓣 ???놁뒿?덈떎. id=" + orderId));
        order.updateInfo(request.getProductId(), request.getOrderNumber(),
                request.getOrderQuantity(), request.getDueDate(),
                request.getProcessStepCount(), request.getToleranceMm(), request.getSkillLevel());
        orderRepository.save(order);
    }

    /**
     * 二쇰Ц????젣?쒕떎. REGISTERED ?곹깭??二쇰Ц留??덉슜?쒕떎.
     *
     * @param orderId ??젣??二쇰Ц ID
     * @throws NoSuchElementException 二쇰Ц??李얠쓣 ???놁쓣 寃쎌슦
     * @throws IllegalStateException  REGISTERED ?곹깭媛 ?꾨땺 寃쎌슦
     */
    @Transactional
    public void delete(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("二쇰Ц??李얠쓣 ???놁뒿?덈떎. id=" + orderId));
        if (order.getStatus() != OrderStatus.REGISTERED) {
            throw new IllegalStateException("REGISTERED ?곹깭??二쇰Ц留???젣?????덉뒿?덈떎. ?꾩옱 ?곹깭: " + order.getStatus());
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

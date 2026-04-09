package com.ohgiraffers.team3backendscm.infrastructure.kafka.listener;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderDifficultyAnalyzedEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.support.OrderKafkaTopics;
import com.ohgiraffers.team3backendscm.scm.command.application.service.OrderDifficultySnapshotCommandService;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderDifficultyAnalyzedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderDifficultyAnalyzedListener.class);

    private final OrderRepository orderRepository;
    private final OrderDifficultySnapshotCommandService orderDifficultySnapshotCommandService;

    @Transactional
    @KafkaListener(
        topics = OrderKafkaTopics.ORDER_DIFFICULTY_ANALYZED,
        containerFactory = "orderDifficultyAnalyzedKafkaListenerContainerFactory"
    )
    public void listen(OrderDifficultyAnalyzedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + event.getOrderId()));

        if (order.getStatus() != OrderStatus.REGISTERED && order.getStatus() != OrderStatus.ANALYZED) {
            log.warn(
                "Skipping order difficulty application because order status is no longer mutable. orderId={}, status={}",
                order.getOrderId(),
                order.getStatus()
            );
            return;
        }

        order.applyDifficultyAnalysis(
            event.getV1ProcessComplexity(),
            event.getV2QualityPrecision(),
            event.getV3CapacityRequirements(),
            event.getV4SpaceTimeUrgency(),
            event.getAlphaNovelty(),
            event.getDifficultyScore(),
            DifficultyGrade.valueOf(event.getDifficultyGrade())
        );

        orderRepository.save(order);
        orderDifficultySnapshotCommandService.publishSnapshotAfterCommit(order);
        log.info(
            "Applied order difficulty analysis result. orderId={}, difficultyGrade={}, difficultyScore={}",
            event.getOrderId(),
            event.getDifficultyGrade(),
            event.getDifficultyScore()
        );
    }
}

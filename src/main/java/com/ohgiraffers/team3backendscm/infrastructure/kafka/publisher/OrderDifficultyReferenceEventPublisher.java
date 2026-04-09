package com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderDifficultySnapshotEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.support.OrderKafkaTopics;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDifficultyReferenceEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderDifficultyReferenceEventPublisher.class);

    private final KafkaTemplate<String, OrderDifficultySnapshotEvent> orderDifficultySnapshotKafkaTemplate;

    public void publishSnapshot(OrderDifficultySnapshotEvent event) {
        orderDifficultySnapshotKafkaTemplate.send(
            OrderKafkaTopics.ORDER_DIFFICULTY_SNAPSHOT,
            String.valueOf(event.getOrderId()),
            event
        );
        log.info(
            "Published order difficulty snapshot event. orderId={}, difficultyGrade={}, difficultyScore={}, orderStatus={}",
            event.getOrderId(),
            event.getDifficultyGrade(),
            event.getDifficultyScore(),
            event.getOrderStatus()
        );
    }
}

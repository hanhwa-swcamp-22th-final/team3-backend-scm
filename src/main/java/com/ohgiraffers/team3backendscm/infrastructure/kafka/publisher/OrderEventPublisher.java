package com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderRegisteredEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.support.OrderKafkaTopics;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, OrderRegisteredEvent> orderRegisteredKafkaTemplate;

    public void publishRegistered(OrderRegisteredEvent event) {
        orderRegisteredKafkaTemplate.send(
            OrderKafkaTopics.ORDER_REGISTERED,
            String.valueOf(event.getOrderId()),
            event
        );
        log.info(
            "Published order registered event. orderId={}, productId={}, configId={}",
            event.getOrderId(),
            event.getProductId(),
            event.getConfigId()
        );
    }
}

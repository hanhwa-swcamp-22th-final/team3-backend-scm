package com.ohgiraffers.team3backendscm.scm.command.application.service;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderDifficultySnapshotEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher.OrderDifficultyReferenceEventPublisher;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class OrderDifficultySnapshotCommandService {

    private final OrderDifficultyReferenceEventPublisher orderDifficultyReferenceEventPublisher;

    public void publishSnapshotAfterCommit(Order order) {
        Runnable publishAction = () -> {
            LocalDateTime now = LocalDateTime.now();
            orderDifficultyReferenceEventPublisher.publishSnapshot(
                new OrderDifficultySnapshotEvent(
                    order.getOrderId(),
                    order.getDifficultyScore(),
                    order.getDifficultyGrade() == null ? null : order.getDifficultyGrade().name(),
                    order.getStatus() == null ? null : order.getStatus().name(),
                    order.getUpdatedAt() == null ? now : order.getUpdatedAt(),
                    now
                )
            );
        };

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

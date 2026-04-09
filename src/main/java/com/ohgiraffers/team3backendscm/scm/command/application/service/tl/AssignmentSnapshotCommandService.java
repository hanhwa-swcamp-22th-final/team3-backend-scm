package com.ohgiraffers.team3backendscm.scm.command.application.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.AssignmentSnapshotEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher.AssignmentReferenceEventPublisher;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class AssignmentSnapshotCommandService {

    private final MatchingRecordRepository matchingRecordRepository;
    private final AssignmentReferenceEventPublisher assignmentReferenceEventPublisher;

    public void publishSnapshotAfterCommit(Long matchingRecordId) {
        Runnable publishAction = () -> matchingRecordRepository.findById(matchingRecordId)
            .ifPresent(this::publishSnapshot);

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

    private void publishSnapshot(MatchingRecord record) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime assignedAt = record.getCreatedAt() == null ? now : record.getCreatedAt();
        assignmentReferenceEventPublisher.publishSnapshot(
            new AssignmentSnapshotEvent(
                record.getMatchingRecordId(),
                record.getOrderId(),
                record.getEmployeeId(),
                record.getStatus() == null ? null : record.getStatus().name(),
                assignedAt,
                now
            )
        );
    }
}

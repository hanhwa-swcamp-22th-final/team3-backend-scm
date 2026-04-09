package com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.AssignmentSnapshotEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.support.OrderKafkaTopics;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentReferenceEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AssignmentReferenceEventPublisher.class);

    private final KafkaTemplate<String, AssignmentSnapshotEvent> assignmentSnapshotKafkaTemplate;

    public void publishSnapshot(AssignmentSnapshotEvent event) {
        assignmentSnapshotKafkaTemplate.send(
            OrderKafkaTopics.ASSIGNMENT_SNAPSHOT,
            String.valueOf(event.getMatchingRecordId()),
            event
        );
        log.info(
            "Published assignment snapshot event. matchingRecordId={}, orderId={}, employeeId={}, matchingStatus={}",
            event.getMatchingRecordId(),
            event.getOrderId(),
            event.getEmployeeId(),
            event.getMatchingStatus()
        );
    }
}

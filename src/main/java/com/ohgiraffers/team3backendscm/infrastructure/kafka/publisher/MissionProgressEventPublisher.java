package com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.MissionProgressEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.support.MissionKafkaTopics;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class MissionProgressEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MissionProgressEventPublisher.class);
    private static final String HIGH_DIFFICULTY_WORK = "HIGH_DIFFICULTY_WORK";

    private final KafkaTemplate<String, MissionProgressEvent> missionProgressKafkaTemplate;

    public void publishHighDifficultyWorkAfterCommit(Long employeeId) {
        MissionProgressEvent event = new MissionProgressEvent(
            employeeId,
            HIGH_DIFFICULTY_WORK,
            BigDecimal.ONE,
            false
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(event);
                }
            });
            return;
        }

        publish(event);
    }

    private void publish(MissionProgressEvent event) {
        missionProgressKafkaTemplate.send(
            MissionKafkaTopics.MISSION_PROGRESS_UPDATED,
            String.valueOf(event.getEmployeeId()),
            event
        );
        log.info(
            "Published mission progress event. employeeId={}, missionType={}, progressValue={}, absolute={}",
            event.getEmployeeId(),
            event.getMissionType(),
            event.getProgressValue(),
            event.getAbsolute()
        );
    }
}

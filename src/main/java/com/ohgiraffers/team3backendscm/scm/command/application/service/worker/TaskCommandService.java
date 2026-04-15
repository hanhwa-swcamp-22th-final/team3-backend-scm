package com.ohgiraffers.team3backendscm.scm.command.application.service.worker;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.publisher.MissionProgressEventPublisher;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskCommandService {

    private final MatchingRecordRepository matchingRecordRepository;
    private final OrderRepository orderRepository;
    private final MissionProgressEventPublisher missionProgressEventPublisher;

    @Transactional
    public void startTask(Long taskId) {
        MatchingRecord record = matchingRecordRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + taskId));

        record.startWork();
        matchingRecordRepository.save(record);
    }

    @Transactional
    public void finishDraft(Long taskId, TaskFinishRequest request) {
        MatchingRecord record = matchingRecordRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + taskId));

        record.finishDraft(request.getComment());
        matchingRecordRepository.save(record);
    }

    @Transactional
    public void finish(Long taskId, TaskFinishRequest request) {
        MatchingRecord record = matchingRecordRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + taskId));

        Order order = orderRepository.findById(record.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + record.getOrderId()));

        record.finish(request.getComment());
        matchingRecordRepository.save(record);

        order.complete();
        orderRepository.save(order);

        if (isHighDifficulty(order.getDifficultyGrade())) {
            missionProgressEventPublisher.publishHighDifficultyWorkAfterCommit(record.getEmployeeId());
        }
    }

    private boolean isHighDifficulty(DifficultyGrade difficultyGrade) {
        return difficultyGrade == DifficultyGrade.D4 || difficultyGrade == DifficultyGrade.D5;
    }
}

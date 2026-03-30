package com.ohgiraffers.team3backendscm.scm.command.application.service.worker;

import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * 작업자(Worker) 권한으로 작업을 실행하는 Command 서비스.
 * <p>
 * 제공 기능:
 * <ul>
 *   <li>작업 시작 (startTask): MatchingRecord.workStartAt 을 현재 시각으로 기록</li>
 *   <li>작업 종료 임시저장 (finishDraft): workEndAt·comment 를 기록하되 상태 유지</li>
 *   <li>작업 종료 제출 (finish): workEndAt·comment 확정 및 COMPLETE 상태 전환, 주문 COMPLETED 처리</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TaskCommandService {

    private final MatchingRecordRepository matchingRecordRepository;
    private final OrderRepository orderRepository;

    /**
     * 작업을 시작한다.
     * MatchingRecord 의 workStartAt 을 현재 시각으로 설정한다.
     * 이미 시작된 작업이면 IllegalStateException 이 발생한다.
     *
     * @param taskId 시작할 작업의 배정 기록 ID (matching_record_id)
     * @throws NoSuchElementException 배정 기록이 없을 경우
     * @throws IllegalStateException  이미 시작된 작업인 경우
     */
    @Transactional
    public void startTask(Long taskId) {
        MatchingRecord record = matchingRecordRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + taskId));

        record.startWork();
        matchingRecordRepository.save(record);
    }

    /**
     * 작업 결과를 임시저장한다.
     * workEndAt·comment 를 기록하지만 상태는 변경하지 않는다.
     *
     * @param taskId  임시저장할 작업의 배정 기록 ID
     * @param request 코멘트를 담은 요청 DTO
     * @throws NoSuchElementException 배정 기록이 없을 경우
     */
    @Transactional
    public void finishDraft(Long taskId, TaskFinishRequest request) {
        MatchingRecord record = matchingRecordRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + taskId));

        record.finishDraft(request.getComment());
        matchingRecordRepository.save(record);
    }

    /**
     * 작업을 완료 제출한다.
     * MatchingRecord 상태를 COMPLETE 로 전환하고, 연관 주문 상태를 COMPLETED 로 변경한다.
     *
     * @param taskId  완료할 작업의 배정 기록 ID
     * @param request 코멘트를 담은 요청 DTO
     * @throws NoSuchElementException 배정 기록 또는 주문이 없을 경우
     */
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
    }
}

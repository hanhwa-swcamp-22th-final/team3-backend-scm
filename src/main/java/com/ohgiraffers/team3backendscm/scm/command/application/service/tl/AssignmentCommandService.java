package com.ohgiraffers.team3backendscm.scm.command.application.service.tl;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.AdminFeignClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ReassignRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * 팀 리더(TL) 권한으로 기술자를 주문에 배정하는 Command 서비스.
 * <p>
 * 배정 처리 흐름:
 * <ol>
 *   <li>대상 주문 조회 및 ANALYZED 상태 검증</li>
 *   <li>동일 중복 배정 여부 확인</li>
 *   <li>Admin API를 통한 기술자 숙련도 티어 조회</li>
 *   <li>주문 난이도 vs 기술자 숙련도 비교로 MatchingMode 자동 결정</li>
 *   <li>주문 상태를 INPROGRESS 로 전이 및 저장</li>
 *   <li>배정 확정 기록(MatchingRecord) 생성 및 저장</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AssignmentCommandService {

    private final OrderRepository orderRepository;
    private final MatchingRecordRepository matchingRecordRepository;
    private final IdGenerator idGenerator;
    private final AdminFeignClient adminFeignClient;
    private final AssignmentSnapshotCommandService assignmentSnapshotCommandService;

    /**
     * 기술자를 주문에 배정한다. 트랜잭션 내에서 주문 상태 변경과 배정 기록 저장이 원자적으로 처리된다.
     *
     * @param request 배정 요청 DTO (orderId, technicianId 포함)
     * @throws NoSuchElementException  주문을 찾을 수 없을 경우
     * @throws IllegalStateException   주문 상태가 ANALYZED 가 아니거나 동일 중복 배정인 경우
     */
    @Transactional
    public void assign(AssignRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("Order not found. id=" + request.getOrderId()));

        String employeeTier = getEmployeeTier(request.getTechnicianId());

        // 난이도 vs 숙련도 비교로 matching_mode 자동 결정
        MatchingMode matchingMode = MatchingMode.determine(order.getDifficultyGrade(), employeeTier);

        // 상태 검증(ANALYZED 아니면 예외) 및 INPROGRESS 전환
        order.assignTechnician(request.getTechnicianId());
        orderRepository.save(order);

        Long recordId = idGenerator.generate();
        MatchingRecord record = new MatchingRecord(recordId, order.getOrderId(), request.getTechnicianId(), matchingMode);
        matchingRecordRepository.save(record);
        assignmentSnapshotCommandService.publishSnapshotAfterCommit(recordId);
    }

    /**
     * 배정된 기술자를 변경(재배치)한다.
     * 새 기술자의 숙련도 티어를 조회하여 MatchingMode 를 재계산한다.
     *
     * @param matchingRecordId 변경할 배정 기록 ID
     * @param request          새 기술자 ID를 담은 요청 DTO
     * @throws NoSuchElementException 배정 기록 또는 주문을 찾을 수 없을 경우
     */
    @Transactional
    public void reassign(Long matchingRecordId, ReassignRequest request) {
        MatchingRecord record = matchingRecordRepository.findById(matchingRecordId)
            .orElseThrow(() -> new NoSuchElementException("Assignment record not found. id=" + matchingRecordId));

        Order order = orderRepository.findById(record.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("Order not found. id=" + record.getOrderId()));

        String employeeTier = getEmployeeTier(request.getTechnicianId());
        MatchingMode newMatchingMode = MatchingMode.determine(order.getDifficultyGrade(), employeeTier);

        record.reassign(request.getTechnicianId(), newMatchingMode);
        matchingRecordRepository.save(record);
        assignmentSnapshotCommandService.publishSnapshotAfterCommit(record.getMatchingRecordId());
    }

    /**
     * 배정을 취소한다.
     * MatchingRecord 상태를 REJECT 로 변경하고 주문 상태를 ANALYZED 로 롤백한다.
     *
     * @param matchingRecordId 취소할 배정 기록 ID
     * @throws NoSuchElementException 배정 기록 또는 주문을 찾을 수 없을 경우
     * @throws IllegalStateException  이미 완료된 배정인 경우
     */
    @Transactional
    public void cancel(Long matchingRecordId) {
        MatchingRecord record = matchingRecordRepository.findById(matchingRecordId)
            .orElseThrow(() -> new NoSuchElementException("Assignment record not found. id=" + matchingRecordId));

        Order order = orderRepository.findById(record.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("Order not found. id=" + record.getOrderId()));

        record.cancel();
        matchingRecordRepository.save(record);
        assignmentSnapshotCommandService.publishSnapshotAfterCommit(record.getMatchingRecordId());

        order.cancelAssignment();
        orderRepository.save(order);
    }

    private String getEmployeeTier(Long employeeId) {
        ApiResponse<AdminEmployeeProfileResponse> response = adminFeignClient.getEmployeeProfile(employeeId);
        if (response == null || !Boolean.TRUE.equals(response.getSuccess()) || response.getData() == null) {
            throw new NoSuchElementException("Employee not found. id=" + employeeId);
        }

        String tier = response.getData().getCurrentTier();
        if (tier == null || tier.isBlank()) {
            throw new IllegalStateException("Employee tier is missing. id=" + employeeId);
        }
        return tier;
    }
}

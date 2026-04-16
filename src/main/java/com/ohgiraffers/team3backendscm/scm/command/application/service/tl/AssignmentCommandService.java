package com.ohgiraffers.team3backendscm.scm.command.application.service.tl;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;
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
 */
@Service
@RequiredArgsConstructor
public class AssignmentCommandService {

    private final OrderRepository orderRepository;
    private final MatchingRecordRepository matchingRecordRepository;
    private final IdGenerator idGenerator;
    private final AssignmentSnapshotCommandService assignmentSnapshotCommandService;
    private final HrClient hrClient;

    /**
     * 기술자를 주문에 배정한다. 트랜잭션 내에서 주문 상태 변경과 배정 기록 저장이 원자적으로 처리된다.
     *
     * @param request 배정 요청 DTO (orderId, technicianId 포함)
     * @throws NoSuchElementException 주문을 찾을 수 없을 경우
     * @throws IllegalStateException 주문 상태가 ANALYZED 가 아니거나 동일 중복 배정인 경우
     */
    @Transactional
    public void assign(AssignRequest request) {
        String employeeTier = getTeamMemberTier(request.getTechnicianId());

        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("Order not found. id=" + request.getOrderId()));

        MatchingMode matchingMode = MatchingMode.determine(order.getDifficultyGrade(), employeeTier);

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
     * @param request 새 기술자 ID를 담은 요청 DTO
     * @throws NoSuchElementException 배정 기록 또는 주문을 찾을 수 없을 경우
     */
    @Transactional
    public void reassign(Long matchingRecordId, ReassignRequest request) {
        String employeeTier = getTeamMemberTier(request.getTechnicianId());

        MatchingRecord record = matchingRecordRepository.findById(matchingRecordId)
            .orElseThrow(() -> new NoSuchElementException("Assignment record not found. id=" + matchingRecordId));

        Order order = orderRepository.findById(record.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("Order not found. id=" + record.getOrderId()));

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
     * @throws IllegalStateException 이미 완료된 배정인 경우
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

    private String getTeamMemberTier(Long technicianId) {
        HrTeamMemberResponse teamMember = hrClient.getTeamMembers().stream()
                .filter(member -> technicianId.equals(member.getEmployeeId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("본인 부서 소속 팀원에게만 주문을 배정할 수 있습니다."));

        String tier = teamMember.getTier();
        if (tier == null || tier.isBlank()) {
            throw new IllegalStateException("Employee tier is missing. id=" + technicianId);
        }
        return tier;
    }
}

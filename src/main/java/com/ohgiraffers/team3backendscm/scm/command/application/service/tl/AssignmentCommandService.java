package com.ohgiraffers.team3backendscm.scm.command.application.service.tl;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ReassignRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import com.ohgiraffers.team3backendscm.scm.query.mapper.EmployeeMapper;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignmentCommandService {

    private final OrderRepository orderRepository;
    private final MatchingRecordRepository matchingRecordRepository;
    private final IdGenerator idGenerator;
    private final EmployeeMapper employeeMapper;
    private final AssignmentSnapshotCommandService assignmentSnapshotCommandService;

    @Transactional
    public void assign(AssignRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + request.getOrderId()));

        String employeeTier = employeeMapper.findTierById(request.getTechnicianId());
        MatchingMode matchingMode = MatchingMode.determine(order.getDifficultyGrade(), employeeTier);

        order.assignTechnician(request.getTechnicianId());
        orderRepository.save(order);

        Long recordId = idGenerator.generate();
        MatchingRecord record = new MatchingRecord(recordId, order.getOrderId(), request.getTechnicianId(), matchingMode);
        matchingRecordRepository.save(record);
        assignmentSnapshotCommandService.publishSnapshotAfterCommit(recordId);
    }

    @Transactional
    public void reassign(Long matchingRecordId, ReassignRequest request) {
        MatchingRecord record = matchingRecordRepository.findById(matchingRecordId)
            .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + matchingRecordId));

        Order order = orderRepository.findById(record.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + record.getOrderId()));

        String employeeTier = employeeMapper.findTierById(request.getTechnicianId());
        MatchingMode newMatchingMode = MatchingMode.determine(order.getDifficultyGrade(), employeeTier);

        record.reassign(request.getTechnicianId(), newMatchingMode);
        matchingRecordRepository.save(record);
        assignmentSnapshotCommandService.publishSnapshotAfterCommit(record.getMatchingRecordId());
    }

    @Transactional
    public void cancel(Long matchingRecordId) {
        MatchingRecord record = matchingRecordRepository.findById(matchingRecordId)
            .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + matchingRecordId));

        Order order = orderRepository.findById(record.getOrderId())
            .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + record.getOrderId()));

        record.cancel();
        matchingRecordRepository.save(record);
        assignmentSnapshotCommandService.publishSnapshotAfterCommit(record.getMatchingRecordId());

        order.cancelAssignment();
        orderRepository.save(order);
    }
}

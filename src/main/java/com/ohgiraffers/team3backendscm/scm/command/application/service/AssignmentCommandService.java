package com.ohgiraffers.team3backendscm.scm.command.application.service;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AssignmentCommandService {

    private final OrderRepository orderRepository;
    private final MatchingRecordRepository matchingRecordRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public void assign(AssignRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다. id=" + request.getOrderId()));

        // 중복 배정 방지 — 당일 이미 배정된 기술자인지 확인
        List<MatchingRecord> existing = matchingRecordRepository
                .findByTechnicianIdAndAssignedDate(request.getTechnicianId(), LocalDate.now());
        if (!existing.isEmpty()) {
            throw new IllegalStateException("이미 해당 날짜에 배정된 기술자입니다. employeeId=" + request.getTechnicianId());
        }

        // 상태 검증 (ANALYZED 아니면 예외) 및 INPROGRESS 전환
        order.assignTechnician(request.getTechnicianId());
        orderRepository.save(order);

        // 배정 확정 기록 저장
        Long recordId = idGenerator.generate();
        MatchingRecord record = new MatchingRecord(recordId, order.getId(), request.getTechnicianId(), LocalDate.now());
        matchingRecordRepository.save(record);
    }
}

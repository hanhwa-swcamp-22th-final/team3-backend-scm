package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingStatus;
import com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository.JpaMatchingRecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 배정 매칭 기록(MatchingRecord) 도메인 레포지토리 인터페이스.
 */
public interface MatchingRecordRepository extends JpaMatchingRecordRepository {

    /**
     * 배정 기록 저장 또는 수정.
     */
    MatchingRecord save(MatchingRecord matchingRecord);

    /**
     * ID로 배정 기록 조회.
     */
    Optional<MatchingRecord> findById(Long matchingRecordId);

    /**
     * 특정 기술자에게 배정된 모든 기록 조회.
     *
     * @param technicianId 조회할 기술자(employee_id)
     * @return 배정 기록 목록
     */
    List<MatchingRecord> findByEmployeeId(Long technicianId);

    /**
     * 특정 기술자에게 특정 날짜에 배정된 기록 조회.
     */
    default List<MatchingRecord> findByTechnicianIdAndAssignedDate(Long technicianId, LocalDate assignedDate) {
        return findByEmployeeIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                technicianId,
                assignedDate.atStartOfDay(),
                assignedDate.plusDays(1).atStartOfDay()
        );
    }

    /**
     * 특정 주문에 대한 배정 기록 조회.
     */
    Optional<MatchingRecord> findByOrderId(Long orderId);

    /**
     * 특정 상태의 모든 배정 기록 조회.
     */
    List<MatchingRecord> findByStatus(MatchingStatus status);

    /**
     * ID로 배정 기록 삭제.
     */
    void deleteById(Long matchingRecordId);
}

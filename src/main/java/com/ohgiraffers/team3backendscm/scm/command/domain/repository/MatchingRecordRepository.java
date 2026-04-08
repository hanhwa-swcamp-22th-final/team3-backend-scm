package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository.JpaMatchingRecordRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * MatchingRecord 에 대한 도메인 리포지토리 인터페이스.
 * JPA 구현체는 {@link JpaMatchingRecordRepository} 에 위임하며,
 * 기술자·날짜 기준 배정 이력 조회 커스텀 쿼리를 추가로 제공한다.
 */
public interface MatchingRecordRepository extends JpaMatchingRecordRepository {

    /**
     * 특정 기술자의 특정 날짜 배정 이력을 조회한다.
     * created_at 컬럼의 날짜 부분과 assignedDate 를 비교하여
     * 당일 중복 배정 여부를 확인하는 데 사용된다.
     *
     * @param technicianId 조회할 기술자(employee_id)
     * @param assignedDate 조회할 날짜 (보통 오늘 날짜)
     * @return 해당 기술자·날짜에 해당하는 배정 기록 목록
     */
    @Query("SELECT m FROM MatchingRecord m WHERE m.employeeId = :technicianId AND FUNCTION('DATE', m.createdAt) = :assignedDate")
    List<MatchingRecord> findByTechnicianIdAndAssignedDate(
            @Param("technicianId") Long technicianId,
            @Param("assignedDate") LocalDate assignedDate);
}

package com.ohgiraffers.team3backendscm.scm.command.domain.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MatchingRecordRepository extends JpaRepository<MatchingRecord, Long> {

    /** created_at 날짜 기준으로 당일 배정 이력 조회 */
    @Query("SELECT m FROM MatchingRecord m WHERE m.employeeId = :technicianId AND FUNCTION('DATE', m.createdAt) = :assignedDate")
    List<MatchingRecord> findByTechnicianIdAndAssignedDate(
            @Param("technicianId") Long technicianId,
            @Param("assignedDate") LocalDate assignedDate);
}

package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JpaMatchingRecordRepository extends JpaRepository<MatchingRecord, Long> {

    @Query("SELECT m FROM MatchingRecord m WHERE m.employeeId = :technicianId AND CAST(m.createdAt AS date) = :assignedDate")
    List<MatchingRecord> findByTechnicianIdAndAssignedDate(
            @Param("technicianId") Long technicianId,
            @Param("assignedDate") LocalDate assignedDate
    );
}

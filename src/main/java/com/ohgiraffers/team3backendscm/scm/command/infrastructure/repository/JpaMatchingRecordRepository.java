package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaMatchingRecordRepository extends JpaRepository<MatchingRecord, Long> {

    List<MatchingRecord> findByEmployeeIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            Long employeeId,
            LocalDateTime startInclusive,
            LocalDateTime endExclusive
    );
}

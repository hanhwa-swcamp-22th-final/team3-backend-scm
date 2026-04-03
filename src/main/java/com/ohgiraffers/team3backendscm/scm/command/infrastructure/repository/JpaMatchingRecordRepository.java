package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMatchingRecordRepository extends JpaRepository<MatchingRecord, Long> {
}

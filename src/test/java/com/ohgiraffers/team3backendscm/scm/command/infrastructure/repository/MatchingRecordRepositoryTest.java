package com.ohgiraffers.team3backendscm.scm.command.infrastructure.repository;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class MatchingRecordRepositoryTest {

    @Autowired
    private MatchingRecordRepository matchingRecordRepository;

    @Test
    @DisplayName("특정 기술자의 오늘 배정 이력을 조회할 수 있다")
    void findByTechnicianAndDate_Success() {
        List<MatchingRecord> result =
                matchingRecordRepository.findByTechnicianIdAndAssignedDate(10L, LocalDate.now());

        assertNotNull(result);
    }
}

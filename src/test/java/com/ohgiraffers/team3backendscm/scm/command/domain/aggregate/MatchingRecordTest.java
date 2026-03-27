package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MatchingRecordTest {

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    @Test
    @DisplayName("배정 기록 생성 시 상태는 CONFIRM이다")
    void createMatchingRecord_StatusIsConfirm() {
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, LocalDate.now());

        assertEquals(MatchingStatus.CONFIRM, record.getStatus());
    }

    @Test
    @DisplayName("이미 배정된 기술자는 동일 날짜에 중복 배정할 수 없다")
    void validateDuplicate_ThrowsWhenAlreadyAssigned() {
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, LocalDate.now());

        assertThrows(IllegalStateException.class,
                () -> record.validateNotDuplicated(10L, LocalDate.now()));
    }
}

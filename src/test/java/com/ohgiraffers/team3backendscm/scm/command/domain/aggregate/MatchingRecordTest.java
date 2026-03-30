package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MatchingRecord 도메인 엔티티의 비즈니스 규칙을 검증하는 단위 테스트.
 *
 * <p>테스트 전략: 순수 단위 테스트 (외부 의존성 없음)
 * - 배정 기록 생성 시 초기 상태(CONFIRM) 검증
 * - 배정 취소(cancel): REJECT 전환 및 완료 배정 취소 불가 검증
 * - 작업 시작(startWork): workStartAt 기록 및 중복 시작 불가 검증
 * - 임시저장(finishDraft): workEndAt·comment 기록 및 상태 미변경 검증
 * - 종료 제출(finish): COMPLETE 전환 검증
 * </p>
 */
class MatchingRecordTest {

    // 시간 기반 고유 ID를 생성하는 유틸리티
    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    @Test
    @DisplayName("배정 기록 생성 시 상태는 CONFIRM이다")
    void createMatchingRecord_StatusIsConfirm() {
        // given - 주문 ID=1, 기술자 ID=10, 효율 우선 모드로 오늘 날짜 배정 기록 생성
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);

        // then - 최초 생성 상태는 CONFIRM 이어야 한다
        assertEquals(MatchingStatus.CONFIRM, record.getStatus());
    }

    @Test
    @DisplayName("CONFIRM 상태의 배정을 취소하면 상태가 REJECT로 변경된다")
    void cancel_ChangesStatusToReject() {
        // given - CONFIRM 상태의 배정 기록 생성
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);

        // when - 배정 취소
        record.cancel();

        // then - 상태가 REJECT로 변경되어야 한다
        assertEquals(MatchingStatus.REJECT, record.getStatus());
    }

    @Test
    @DisplayName("COMPLETE 상태의 배정은 취소할 수 없다")
    void cancel_ThrowsWhenAlreadyComplete() {
        // given - COMPLETE 상태에 도달시키기 위해 finish() 호출
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);
        record.finish("작업 완료");

        // when & then - 완료된 배정을 취소 시도하면 IllegalStateException 발생
        assertThrows(IllegalStateException.class, record::cancel);
    }

    @Test
    @DisplayName("작업 시작 시 workStartAt이 기록된다")
    void startWork_SetsWorkStartAt() {
        // given - CONFIRM 상태의 배정 기록 생성
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);

        // when - 작업 시작
        record.startWork();

        // then - workStartAt이 null이 아닌 현재 시각으로 기록되어야 한다
        assertNotNull(record.getWorkStartAt());
    }

    @Test
    @DisplayName("이미 시작된 작업을 다시 시작하면 예외가 발생한다")
    void startWork_ThrowsWhenAlreadyStarted() {
        // given - 이미 한 번 시작된 배정 기록
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);
        record.startWork();

        // when & then - 중복 시작 시도 시 IllegalStateException 발생
        assertThrows(IllegalStateException.class, record::startWork);
    }

    @Test
    @DisplayName("작업 임시저장 시 workEndAt과 comment가 기록되고 상태는 유지된다")
    void finishDraft_RecordsEndAtAndCommentWithoutStatusChange() {
        // given - CONFIRM 상태의 배정 기록
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);

        // when - 임시저장 처리
        record.finishDraft("임시 저장 코멘트");

        // then - workEndAt과 comment가 기록되고, 상태는 CONFIRM 유지
        assertNotNull(record.getWorkEndAt());
        assertEquals("임시 저장 코멘트", record.getComment());
        assertEquals(MatchingStatus.CONFIRM, record.getStatus());
    }

    @Test
    @DisplayName("작업 종료 제출 시 상태가 COMPLETE로 전환된다")
    void finish_ChangesStatusToComplete() {
        // given - CONFIRM 상태의 배정 기록
        MatchingRecord record = new MatchingRecord(idGenerator.generate(), 1L, 10L, MatchingMode.EFFICIENCY_TYPE);

        // when - 작업 종료 제출
        record.finish("최종 완료 코멘트");

        // then - 상태가 COMPLETE로 전환되어야 한다
        assertEquals(MatchingStatus.COMPLETE, record.getStatus());
        assertEquals("최종 완료 코멘트", record.getComment());
        assertNotNull(record.getWorkEndAt());
    }
}

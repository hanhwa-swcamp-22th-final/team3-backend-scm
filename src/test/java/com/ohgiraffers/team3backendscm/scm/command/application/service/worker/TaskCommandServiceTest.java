package com.ohgiraffers.team3backendscm.scm.command.application.service.worker;

import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.Order;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.MatchingRecordRepository;
import com.ohgiraffers.team3backendscm.scm.command.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 작업자(Worker) 작업 실행 Command 서비스 단위 테스트.
 *
 * <p>테스트 전략: @ExtendWith(MockitoExtension) — Mockito로 Repository를 모킹하여
 * 서비스 로직만 순수하게 검증한다.
 * - 작업 시작(startTask): INPROGRESS 전환, workStartAt 기록 및 저장 검증
 * - 작업 임시저장(finishDraft): workEndAt·comment 기록 및 저장 검증
 * - 작업 종료 제출(finish): COMPLETE 전환 + 주문 COMPLETED 처리 검증
 * - 예외: 존재하지 않는 배정 기록 ID 처리 검증
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class TaskCommandServiceTest {

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    @Mock
    private MatchingRecordRepository matchingRecordRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private TaskCommandService taskCommandService;

    @Nested
    @DisplayName("작업 시작")
    class StartTask {

        @Test
        @DisplayName("성공: 배정 기록이 INPROGRESS로 전환되고 workStartAt이 기록된다")
        void startTask_Success() {
            // given
            Long taskId = idGenerator.generate();
            MatchingRecord record = new MatchingRecord(taskId, idGenerator.generate(), 10L, MatchingMode.EFFICIENCY_TYPE);
            given(matchingRecordRepository.findById(taskId)).willReturn(Optional.of(record));

            // when
            taskCommandService.startTask(taskId);

            // then - 상태가 진행 중으로 전환되고 workStartAt 이 현재 시각으로 기록되어야 한다
            assertEquals(MatchingStatus.INPROGRESS, record.getStatus());
            assertNotNull(record.getWorkStartAt());
            verify(matchingRecordRepository, times(1)).save(any(MatchingRecord.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 작업 ID로 시작 요청 시 예외가 발생한다")
        void startTask_Fail_WhenNotFound() {
            // given
            given(matchingRecordRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> taskCommandService.startTask(999L));
            verify(matchingRecordRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("작업 종료 임시저장")
    class FinishDraft {

        @Test
        @DisplayName("성공: workEndAt과 comment가 기록되고 저장된다")
        void finishDraft_Success() {
            // given
            Long taskId = idGenerator.generate();
            MatchingRecord record = new MatchingRecord(taskId, idGenerator.generate(), 10L, MatchingMode.EFFICIENCY_TYPE);
            given(matchingRecordRepository.findById(taskId)).willReturn(Optional.of(record));

            // when
            taskCommandService.finishDraft(taskId, new TaskFinishRequest("임시 코멘트"));

            // then - comment가 기록된 상태로 저장되어야 한다
            verify(matchingRecordRepository, times(1)).save(any(MatchingRecord.class));
            assertEquals("임시 코멘트", record.getComment());
            // 상태는 CONFIRM 유지
            assertEquals(MatchingStatus.CONFIRM, record.getStatus());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 작업 ID로 임시저장 요청 시 예외가 발생한다")
        void finishDraft_Fail_WhenNotFound() {
            // given
            given(matchingRecordRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> taskCommandService.finishDraft(999L, new TaskFinishRequest("코멘트")));
        }
    }

    @Nested
    @DisplayName("작업 종료 제출")
    class Finish {

        @Test
        @DisplayName("성공: MatchingRecord는 COMPLETE로, 주문은 COMPLETED로 전환된다")
        void finish_Success() {
            // given
            Long taskId  = idGenerator.generate();
            Long orderId = idGenerator.generate();
            MatchingRecord record = new MatchingRecord(taskId, orderId, 10L, MatchingMode.EFFICIENCY_TYPE);
            Order order = new Order(orderId, "ORD-0401", OrderStatus.INPROGRESS, LocalDate.now().plusDays(5));

            given(matchingRecordRepository.findById(taskId)).willReturn(Optional.of(record));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            taskCommandService.finish(taskId, new TaskFinishRequest("최종 코멘트"));

            // then - MatchingRecord와 Order 각 1회씩 저장 확인
            verify(matchingRecordRepository, times(1)).save(any(MatchingRecord.class));
            verify(orderRepository, times(1)).save(any(Order.class));
            // 상태 전환 확인
            assertEquals(MatchingStatus.COMPLETE, record.getStatus());
            assertEquals(OrderStatus.COMPLETED, order.getStatus());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 작업 ID로 종료 제출 시 예외가 발생한다")
        void finish_Fail_WhenNotFound() {
            // given
            given(matchingRecordRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> taskCommandService.finish(999L, new TaskFinishRequest("코멘트")));
            verify(orderRepository, never()).findById(any());
        }
    }
}

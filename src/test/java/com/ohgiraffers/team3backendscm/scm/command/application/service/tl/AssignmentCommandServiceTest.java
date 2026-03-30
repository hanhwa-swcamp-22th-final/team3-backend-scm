package com.ohgiraffers.team3backendscm.scm.command.application.service.tl;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.ReassignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.tl.AssignmentCommandService;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignmentCommandServiceTest {

    private final TimeBasedIdGenerator idGenerator = new TimeBasedIdGenerator();

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MatchingRecordRepository matchingRecordRepository;
    @Mock
    private IdGenerator mockIdGenerator;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AssignmentCommandService assignmentCommandService;

    @Nested
    @DisplayName("작업 배정 확정")
    class Assign {

        @Test
        @DisplayName("성공: ANALYZED 주문에 기술자를 배정하면 MatchingRecord가 저장된다")
        void assign_Success() {
            // given
            Order order = new Order(idGenerator.generate(), "ORD-0301", OrderStatus.ANALYZED, LocalDate.now().plusDays(5));
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));
            given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(10L)))
                    .willReturn("A");
            given(mockIdGenerator.generate()).willReturn(idGenerator.generate());

            // when
            assignmentCommandService.assign(new AssignRequest(1L, 10L));

            // then
            verify(matchingRecordRepository, times(1)).save(any(MatchingRecord.class));
        }

        @Test
        @DisplayName("성공: 역량(A) < 난이도(D5=S) → GROWTH_TYPE으로 저장된다")
        void assign_GrowthType_WhenTierBelowRequired() {
            // given
            Order order = new Order(idGenerator.generate(), "ORD-D5", OrderStatus.ANALYZED, LocalDate.now().plusDays(5), DifficultyGrade.D5);
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));
            given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(10L)))
                    .willReturn("A"); // A(2) < S(3) → GROWTH_TYPE
            given(mockIdGenerator.generate()).willReturn(idGenerator.generate());

            // when
            assignmentCommandService.assign(new AssignRequest(1L, 10L));

            // then
            ArgumentCaptor<MatchingRecord> captor = ArgumentCaptor.forClass(MatchingRecord.class);
            verify(matchingRecordRepository).save(captor.capture());
            assertEquals(MatchingMode.GROWTH_TYPE, captor.getValue().getMatchingMode());
        }

        @Test
        @DisplayName("성공: 역량(S) >= 난이도(D5=S) → EFFICIENCY_TYPE으로 저장된다")
        void assign_EfficiencyType_WhenTierMeetsRequired() {
            // given
            Order order = new Order(idGenerator.generate(), "ORD-D5", OrderStatus.ANALYZED, LocalDate.now().plusDays(5), DifficultyGrade.D5);
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));
            given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(10L)))
                    .willReturn("S"); // S(3) >= S(3) → EFFICIENCY_TYPE
            given(mockIdGenerator.generate()).willReturn(idGenerator.generate());

            // when
            assignmentCommandService.assign(new AssignRequest(1L, 10L));

            // then
            ArgumentCaptor<MatchingRecord> captor = ArgumentCaptor.forClass(MatchingRecord.class);
            verify(matchingRecordRepository).save(captor.capture());
            assertEquals(MatchingMode.EFFICIENCY_TYPE, captor.getValue().getMatchingMode());
        }

        @Test
        @DisplayName("실패: ANALYZED 상태가 아닌 주문에는 배정할 수 없다")
        void assign_Fail_WhenNotAnalyzed() {
            // given
            Order order = new Order(idGenerator.generate(), "ORD-0301", OrderStatus.REGISTERED, LocalDate.now().plusDays(5));
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));
            given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(10L)))
                    .willReturn("A");

            // when & then
            assertThrows(IllegalStateException.class,
                    () -> assignmentCommandService.assign(new AssignRequest(1L, 10L)));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 주문 ID로 배정 시 예외가 발생한다")
        void assign_Fail_WhenOrderNotFound() {
            // given
            given(orderRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> assignmentCommandService.assign(new AssignRequest(999L, 10L)));
        }
    }

    @Nested
    @DisplayName("기술자 재배정")
    class Reassign {

        @Test
        @DisplayName("성공: 새 기술자 ID로 배정 기록이 갱신된다")
        void reassign_Success() {
            // given
            Long recordId = idGenerator.generate();
            Long orderId  = idGenerator.generate();
            MatchingRecord record = new MatchingRecord(recordId, orderId, 10L, MatchingMode.EFFICIENCY_TYPE);
            Order order = new Order(orderId, "ORD-0302", OrderStatus.INPROGRESS, LocalDate.now().plusDays(5), DifficultyGrade.D5);

            given(matchingRecordRepository.findById(recordId)).willReturn(Optional.of(record));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(20L))).willReturn("S");

            // when
            assignmentCommandService.reassign(recordId, new ReassignRequest(20L));

            // then - 변경된 기록이 저장되어야 한다
            ArgumentCaptor<MatchingRecord> captor = ArgumentCaptor.forClass(MatchingRecord.class);
            verify(matchingRecordRepository).save(captor.capture());
            assertEquals(20L, captor.getValue().getEmployeeId());
            assertEquals(MatchingMode.EFFICIENCY_TYPE, captor.getValue().getMatchingMode());
        }

        @Test
        @DisplayName("성공: 새 기술자 티어가 낮으면 GROWTH_TYPE으로 재산정된다")
        void reassign_GrowthType_WhenNewTierBelowRequired() {
            // given
            Long recordId = idGenerator.generate();
            Long orderId  = idGenerator.generate();
            MatchingRecord record = new MatchingRecord(recordId, orderId, 10L, MatchingMode.EFFICIENCY_TYPE);
            Order order = new Order(orderId, "ORD-D5", OrderStatus.INPROGRESS, LocalDate.now().plusDays(5), DifficultyGrade.D5);

            given(matchingRecordRepository.findById(recordId)).willReturn(Optional.of(record));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(20L))).willReturn("B"); // B(1) < S(3)

            // when
            assignmentCommandService.reassign(recordId, new ReassignRequest(20L));

            // then
            ArgumentCaptor<MatchingRecord> captor = ArgumentCaptor.forClass(MatchingRecord.class);
            verify(matchingRecordRepository).save(captor.capture());
            assertEquals(MatchingMode.GROWTH_TYPE, captor.getValue().getMatchingMode());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 배정 기록 ID로 재배정 시 예외가 발생한다")
        void reassign_Fail_WhenRecordNotFound() {
            // given
            given(matchingRecordRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> assignmentCommandService.reassign(999L, new ReassignRequest(20L)));
        }
    }

    @Nested
    @DisplayName("배정 취소")
    class Cancel {

        @Test
        @DisplayName("성공: 배정 취소 후 MatchingRecord는 REJECT, 주문은 ANALYZED로 롤백된다")
        void cancel_Success() {
            // given
            Long recordId = idGenerator.generate();
            Long orderId  = idGenerator.generate();
            MatchingRecord record = new MatchingRecord(recordId, orderId, 10L, MatchingMode.EFFICIENCY_TYPE);
            Order order = new Order(orderId, "ORD-0303", OrderStatus.INPROGRESS, LocalDate.now().plusDays(5));

            given(matchingRecordRepository.findById(recordId)).willReturn(Optional.of(record));
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            assignmentCommandService.cancel(recordId);

            // then - 상태 전환 검증
            assertEquals(MatchingStatus.REJECT, record.getStatus());
            assertEquals(OrderStatus.ANALYZED, order.getStatus());
            // then - MatchingRecord 와 Order 각 1회씩 저장 확인
            verify(matchingRecordRepository, times(1)).save(any(MatchingRecord.class));
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 배정 기록 ID로 취소 시 예외가 발생한다")
        void cancel_Fail_WhenRecordNotFound() {
            // given
            given(matchingRecordRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class,
                    () -> assignmentCommandService.cancel(999L));
        }
    }
}

package com.ohgiraffers.team3backendscm.scm.command.application.service;

import com.ohgiraffers.team3backendscm.common.idgenerator.IdGenerator;
import com.ohgiraffers.team3backendscm.common.idgenerator.TimeBasedIdGenerator;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingRecord;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
            given(matchingRecordRepository.findByTechnicianIdAndAssignedDate(10L, LocalDate.now()))
                    .willReturn(List.of());
            given(mockIdGenerator.generate()).willReturn(idGenerator.generate());

            // when
            assignmentCommandService.assign(new AssignRequest(1L, 10L));

            // then
            verify(matchingRecordRepository, times(1)).save(any(MatchingRecord.class));
        }

        @Test
        @DisplayName("실패: ANALYZED 상태가 아닌 주문에는 배정할 수 없다")
        void assign_Fail_WhenNotAnalyzed() {
            // given
            Order order = new Order(idGenerator.generate(), "ORD-0301", OrderStatus.REGISTERED, LocalDate.now().plusDays(5));
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));
            given(matchingRecordRepository.findByTechnicianIdAndAssignedDate(10L, LocalDate.now()))
                    .willReturn(List.of());

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

        @Test
        @DisplayName("실패: 당일 이미 배정된 기술자는 중복 배정할 수 없다")
        void assign_Fail_WhenTechnicianAlreadyAssigned() {
            // given
            Order order = new Order(idGenerator.generate(), "ORD-0301", OrderStatus.ANALYZED, LocalDate.now().plusDays(5));
            given(orderRepository.findById(1L)).willReturn(Optional.of(order));
            given(matchingRecordRepository.findByTechnicianIdAndAssignedDate(10L, LocalDate.now()))
                    .willReturn(List.of(new MatchingRecord(idGenerator.generate(), 1L, 10L, LocalDate.now())));

            // when & then
            assertThrows(IllegalStateException.class,
                    () -> assignmentCommandService.assign(new AssignRequest(1L, 10L)));
        }
    }
}

package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.AdminClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentCandidateDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceWorkerRow;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentTimelineDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.AssignmentMapper;
import com.ohgiraffers.team3backendscm.scm.query.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignmentQueryServiceTest {

    @Mock
    private AssignmentMapper assignmentMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private AdminClient adminClient;
    @Mock
    private HrClient hrClient;

    @InjectMocks
    private AssignmentQueryService assignmentQueryService;

    @Test
    @DisplayName("배정 상세 조회 시 Mapper가 1회 호출되고 결과를 반환한다")
    void getAssignment_CallsMapperOnce() {
        // given
        AssignmentDetailDto dto = new AssignmentDetailDto();
        given(assignmentMapper.findById(1L)).willReturn(Optional.of(dto));

        // when
        assignmentQueryService.getAssignment(1L);

        // then
        verify(assignmentMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 배정 기록 ID 조회 시 NoSuchElementException이 발생한다")
    void getAssignment_ThrowsWhenNotFound() {
        // given
        given(assignmentMapper.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class,
                () -> assignmentQueryService.getAssignment(999L));
    }

    @Test
    @DisplayName("배정 후보 조회 시 HR 팀원 목록 기준으로 후보 Mapper를 호출한다")
    void getCandidates_CallsMapperWithTeamMembers() {
        // given
        HrTeamMemberResponse member = mock(HrTeamMemberResponse.class);
        given(member.getEmployeeId()).willReturn(10L);
        given(hrClient.getTeamMembers()).willReturn(List.of(member));
        given(assignmentMapper.findCandidatesByEmployeeIds(List.of(10L), 100L))
                .willReturn(List.of(new AssignmentCandidateDto(
                        10L,
                        "김작업",
                        "A",
                        new BigDecimal("91.0"),
                        null,
                        null
                )));

        // when
        List<AssignmentCandidateDto> result = assignmentQueryService.getCandidates(100L);

        // then
        assertEquals(10L, result.get(0).getEmployeeId());
        verify(assignmentMapper, times(1)).findCandidatesByEmployeeIds(List.of(10L), 100L);
        verify(assignmentMapper, never()).findSummary();
    }

    @Test
    @DisplayName("배정 후보 조회 시 HR 팀원 목록이 비어 있으면 빈 목록을 반환하고 Mapper를 호출하지 않는다")
    void getCandidates_ReturnsEmpty_WhenNoTeamMembers() {
        // given
        given(hrClient.getTeamMembers()).willReturn(List.of());

        // when
        List<AssignmentCandidateDto> result = assignmentQueryService.getCandidates(100L);

        // then
        assertEquals(0, result.size());
        verify(assignmentMapper, never()).findCandidatesByEmployeeIds(anyList(), any());
    }

    @Test
    @DisplayName("주문 ID가 있으면 OCSA 난이도와 티어 기준으로 적합도와 배정 유형을 계산한다")
    void getCandidates_WithOrderId_CalculatesSuitabilityAndMatchingMode() {
        // given
        HrTeamMemberResponse member1 = mock(HrTeamMemberResponse.class);
        HrTeamMemberResponse member2 = mock(HrTeamMemberResponse.class);
        given(member1.getEmployeeId()).willReturn(10L);
        given(member2.getEmployeeId()).willReturn(11L);
        given(hrClient.getTeamMembers()).willReturn(List.of(member1, member2));
        given(orderMapper.findOrderOcsa(1L))
                .willReturn(new OrderOcsaDto(1L, null, null, null, null, null, null, DifficultyGrade.D5, null));
        given(assignmentMapper.findCandidatesByEmployeeIds(List.of(10L, 11L), 1L))
                .willReturn(List.of(
                        new AssignmentCandidateDto(10L, "김에스", "S", new BigDecimal("90.0"), null, null),
                        new AssignmentCandidateDto(11L, "김비", "B", new BigDecimal("90.0"), null, null)
                ));

        // when
        List<AssignmentCandidateDto> result = assignmentQueryService.getCandidates(1L);

        // then
        assertEquals(10L, result.get(0).getEmployeeId());
        assertEquals(MatchingMode.EFFICIENCY_TYPE, result.get(0).getMatchingMode());
        assertEquals(MatchingMode.GROWTH_TYPE, result.get(1).getMatchingMode());
        assertEquals(new BigDecimal("0.9300"), result.get(0).getSuitabilityScore());
    }

    @Test
    @DisplayName("배정 현황 집계 조회 시 Mapper가 1회 호출된다")
    void getSummary_CallsMapperOnce() {
        // given
        given(assignmentMapper.findSummary()).willReturn(new AssignmentSummaryDto());

        // when
        assignmentQueryService.getSummary();

        // then
        verify(assignmentMapper, times(1)).findSummary();
    }

    @Test
    @DisplayName("배정 타임라인 조회 시 Mapper가 1회 호출된다")
    void getTimeline_CallsMapperOnce() {
        // given
        given(assignmentMapper.findTimeline()).willReturn(List.of());

        // when
        assignmentQueryService.getTimeline();

        // then
        verify(assignmentMapper, times(1)).findTimeline();
    }

    @Test
    @DisplayName("배정 타임라인 조회 시 Admin Feign으로 직원명과 티어를 병합한다")
    void getTimeline_EnrichesEmployeeProfile() {
        // given
        AssignmentTimelineDto timeline = new AssignmentTimelineDto(
                null,
                null,
                10L,
                null,
                null,
                LocalDate.now(),
                "INPROGRESS",
                "ORD-1",
                "INPROGRESS",
                LocalDateTime.now().minusHours(1),
                null
        );
        given(assignmentMapper.findTimeline()).willReturn(List.of(timeline));
        AdminEmployeeProfileResponse profile = new AdminEmployeeProfileResponse();
        profile.setEmployeeId(10L);
        profile.setEmployeeName("김작업");
        profile.setCurrentTier("A");
        given(adminClient.getEmployeeProfile(10L)).willReturn(profile);

        // when
        List<AssignmentTimelineDto> result = assignmentQueryService.getTimeline();

        // then
        assertEquals("김작업", result.get(0).getEmployeeName());
        assertEquals("A", result.get(0).getEmployeeTier());
        assertEquals("ORD-1", result.get(0).getOrderNo());
        verify(adminClient, times(1)).getEmployeeProfile(10L);
    }

    @Test
    @DisplayName("재배치 권고 조회 시 Mapper가 1회 호출된다")
    void getRebalance_CallsMapperOnce() {
        // given
        AssignmentRebalanceWorkerRow row = new AssignmentRebalanceWorkerRow();
        row.setFactoryLineId(1L);
        row.setFactoryLineName("Main Line");
        row.setEmployeeId(10L);
        given(assignmentMapper.findRebalanceWorkers()).willReturn(List.of(row));
        AdminEmployeeProfileResponse profile = new AdminEmployeeProfileResponse();
        profile.setEmployeeId(10L);
        profile.setCurrentTier("B");
        given(adminClient.getEmployeeProfile(10L)).willReturn(profile);

        // when
        assignmentQueryService.getRebalance();

        // then
        verify(assignmentMapper, times(1)).findRebalanceWorkers();
    }
}

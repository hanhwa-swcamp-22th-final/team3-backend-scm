package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.AssignmentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssignmentQueryServiceTest {

    @Mock
    private AssignmentMapper assignmentMapper;

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
    @DisplayName("배정 후보 조회 시 Mapper가 1회 호출된다")
    void getCandidates_CallsMapperOnce() {
        // given
        given(assignmentMapper.findCandidates()).willReturn(List.of());

        // when
        assignmentQueryService.getCandidates();

        // then
        verify(assignmentMapper, times(1)).findCandidates();
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
    @DisplayName("재배치 권고 조회 시 Mapper가 1회 호출된다")
    void getRebalance_CallsMapperOnce() {
        // given
        given(assignmentMapper.findRebalance()).willReturn(List.of());

        // when
        assignmentQueryService.getRebalance();

        // then
        verify(assignmentMapper, times(1)).findRebalance();
    }
}

package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineWorkerDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.LineMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineQueryServiceTest {

    @Mock
    private LineMapper lineMapper;
    @Mock
    private HrClient hrClient;

    @InjectMocks
    private LineQueryService lineQueryService;

    @Test
    @DisplayName("라인 요약 조회 시 Mapper가 1회 호출된다")
    void getLinesSummary_CallsMapperOnce() {
        // given
        given(lineMapper.findLinesSummary()).willReturn(List.of());

        // when
        lineQueryService.getLinesSummary();

        // then
        verify(lineMapper, times(1)).findLinesSummary();
    }

    @Test
    @DisplayName("라인 상태 조회 시 Mapper가 1회 호출된다")
    void getLineStatus_CallsMapperOnce() {
        // given
        given(lineMapper.findLineStatus(anyLong())).willReturn(new LineStatusDto());

        // when
        lineQueryService.getLineStatus(1L);

        // then
        verify(lineMapper, times(1)).findLineStatus(anyLong());
    }

    @Test
    @DisplayName("라인 작업자 조회 시 HR 팀원 목록 기준으로 Mapper를 호출한다")
    void getLineWorkers_CallsMapperWithTeamMembers() {
        // given
        HrTeamMemberResponse member = mock(HrTeamMemberResponse.class);
        given(member.getEmployeeId()).willReturn(10L);
        given(hrClient.getTeamMembers()).willReturn(List.of(member));
        given(lineMapper.findLineWorkersByEmployeeIds(1L, List.of(10L)))
                .willReturn(List.of(new LineWorkerDto()));

        // when
        lineQueryService.getLineWorkers(1L);

        // then
        verify(lineMapper, times(1)).findLineWorkersByEmployeeIds(1L, List.of(10L));
        verify(lineMapper, never()).findLineWorkers(1L);
    }

    @Test
    @DisplayName("라인 작업자 조회 시 팀원 기준 결과가 비어 있으면 전체 라인 작업자로 폴백한다")
    void getLineWorkers_FallbacksToAllLineWorkers_WhenTeamWorkersEmpty() {
        // given
        HrTeamMemberResponse member = mock(HrTeamMemberResponse.class);
        given(member.getEmployeeId()).willReturn(10L);
        given(hrClient.getTeamMembers()).willReturn(List.of(member));
        given(lineMapper.findLineWorkersByEmployeeIds(1L, List.of(10L))).willReturn(List.of());
        given(lineMapper.findLineWorkers(1L)).willReturn(List.of());

        // when
        lineQueryService.getLineWorkers(1L);

        // then
        verify(lineMapper, times(1)).findLineWorkersByEmployeeIds(1L, List.of(10L));
        verify(lineMapper, times(1)).findLineWorkers(1L);
    }
}

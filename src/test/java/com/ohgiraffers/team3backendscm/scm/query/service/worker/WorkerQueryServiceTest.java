package com.ohgiraffers.team3backendscm.scm.query.service.worker;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerTaskSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.WorkerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkerQueryServiceTest {

    @Mock
    private WorkerMapper workerMapper;

    @InjectMocks
    private WorkerQueryService workerQueryService;

    @Test
    @DisplayName("미완료 작업 목록 조회 시 Mapper가 1회 호출되고 결과를 반환한다")
    void getMyPendingTasks_CallsMapperOnce() {
        // given - 미완료 작업 1건이 있는 상황
        TaskDto task = new TaskDto();
        given(workerMapper.findMyPendingTasks(anyLong())).willReturn(List.of(task));

        // when
        List<TaskDto> result = workerQueryService.getMyPendingTasks(10L);

        // then
        verify(workerMapper, times(1)).findMyPendingTasks(anyLong());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("내 작업 배치 현황 조회 시 Mapper가 1회 호출된다")
    void getMyDeployments_CallsMapperOnce() {
        // given
        given(workerMapper.findMyDeployments(anyLong())).willReturn(List.of());

        // when
        workerQueryService.getMyDeployments(10L);

        // then
        verify(workerMapper, times(1)).findMyDeployments(anyLong());
    }

    @Test
    @DisplayName("내 매칭 이력 조회 시 Mapper가 1회 호출된다")
    void getMyMatchingHistory_CallsMapperOnce() {
        // given
        given(workerMapper.findMyMatchingHistory(anyLong())).willReturn(List.of());

        // when
        workerQueryService.getMyMatchingHistory(10L);

        // then
        verify(workerMapper, times(1)).findMyMatchingHistory(anyLong());
    }

    @Test
    @DisplayName("작업 현황 집계 조회 시 Mapper가 1회 호출되고 결과를 반환한다")
    void getMyTaskSummary_ReturnsSummary() {
        // given
        WorkerTaskSummaryDto mockSummary = new WorkerTaskSummaryDto(2, 1, 5);
        given(workerMapper.findMyTaskSummary(anyLong())).willReturn(mockSummary);

        // when
        WorkerTaskSummaryDto result = workerQueryService.getMyTaskSummary(10L);

        // then
        verify(workerMapper, times(1)).findMyTaskSummary(anyLong());
        assertEquals(2, result.getAssignedCount());
        assertEquals(1, result.getInProgressCount());
        assertEquals(5, result.getCompletedCount());
    }
}

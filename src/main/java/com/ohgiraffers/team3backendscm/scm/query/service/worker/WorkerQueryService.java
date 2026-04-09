package com.ohgiraffers.team3backendscm.scm.query.service.worker;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerTaskSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 작업자 권한에서 본인 관련 이력을 조회하는 Query 서비스이다.
 */
@Service
@RequiredArgsConstructor
public class WorkerQueryService {

    private final WorkerMapper workerMapper;

    /**
     * 본인의 미완료 작업 목록을 조회한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 미완료 작업 목록
     */
    public List<TaskDto> getMyPendingTasks(Long employeeId) {
        return workerMapper.findMyPendingTasks(employeeId);
    }

    /**
     * 본인의 설비 배치 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 설비 배치 이력 목록
     */
    public List<WorkerDeploymentDto> getMyDeployments(Long employeeId) {
        return workerMapper.findMyDeployments(employeeId);
    }

    /**
     * 본인의 주문 배정 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 주문 배정 이력 목록
     */
    public List<WorkerMatchingHistoryDto> getMyMatchingHistory(Long employeeId) {
        return workerMapper.findMyMatchingHistory(employeeId);
    }

    /**
     * 본인의 작업 상태별 집계를 조회한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 작업 상태 집계 DTO
     */
    public WorkerTaskSummaryDto getMyTaskSummary(Long employeeId) {
        return workerMapper.findMyTaskSummary(employeeId);
    }
}
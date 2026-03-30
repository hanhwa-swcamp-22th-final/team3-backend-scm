package com.ohgiraffers.team3backendscm.scm.query.service.worker;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 작업자(Worker) 권한의 본인 이력 조회 Query 서비스.
 * WorkerMapper 를 통해 작업자 자신의 설비 배치 이력과 주문 배정 이력을 읽기 전용으로 제공한다.
 */
@Service
@RequiredArgsConstructor
public class WorkerQueryService {

    private final WorkerMapper workerMapper;

    /**
     * 특정 작업자의 미완료 작업 목록을 조회한다.
     * 날짜 무관하게 REJECT·COMPLETE 상태를 제외한 전체 배정 작업을 반환한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 미완료 작업 목록
     */
    public List<TaskDto> getMyPendingTasks(Long employeeId) {
        return workerMapper.findMyPendingTasks(employeeId);
    }

    /**
     * 특정 작업자의 설비 배치 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 설비 배치 이력 목록
     */
    public List<WorkerDeploymentDto> getMyDeployments(Long employeeId) {
        return workerMapper.findMyDeployments(employeeId);
    }

    /**
     * 특정 작업자의 주문 배정 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 주문 배정 이력 목록
     */
    public List<WorkerMatchingHistoryDto> getMyMatchingHistory(Long employeeId) {
        return workerMapper.findMyMatchingHistory(employeeId);
    }
}

package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerTaskSummaryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 작업자(Worker) 본인 조회 전용 MyBatis 매퍼 인터페이스.
 * 작업자가 자신의 설비 배치 이력과 주문 배정 이력을 조회할 때 사용한다.
 * SQL은 src/main/resources/mapper/workers.xml 에 정의한다.
 */
@Mapper
public interface WorkerMapper {

    /**
     * 특정 작업자의 미완료 작업 목록을 조회한다.
     * matching_record, orders, product 테이블을 JOIN하여 REJECT·COMPLETE 상태를 제외한 전체 배정 작업을 반환한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 미완료 작업 목록 (TaskDto 리스트)
     */
    List<TaskDto> findMyPendingTasks(Long employeeId);

    /**
     * 특정 작업자(employeeId)의 설비 배치 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 설비 배치 이력 목록
     */
    List<WorkerDeploymentDto> findMyDeployments(Long employeeId);

    /**
     * 특정 작업자(employeeId)의 주문 배정 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 주문 배정 이력 목록
     */
    List<WorkerMatchingHistoryDto> findMyMatchingHistory(Long employeeId);

    /**
     * 특정 작업자의 상태별 작업 수를 집계한다.
     * CONFIRM·INPROGRESS·COMPLETE 상태별 카운트를 반환한다.
     *
     * @param employeeId 조회할 작업자(직원) ID
     * @return 상태별 작업 수 집계 DTO
     */
    WorkerTaskSummaryDto findMyTaskSummary(Long employeeId);
}

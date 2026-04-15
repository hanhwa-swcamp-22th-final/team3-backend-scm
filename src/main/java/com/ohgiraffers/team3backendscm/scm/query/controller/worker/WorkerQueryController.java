package com.ohgiraffers.team3backendscm.scm.query.controller.worker;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerTaskSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.service.worker.WorkerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 작업자(Worker) 권한의 본인 이력 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>GET /tasks - 본인 미완료 작업 목록 조회</li>
 *   <li>GET /tasks/summary - 본인 상태별 작업 수 집계 조회</li>
 *   <li>GET /workers/me/deployments - 본인 설비 배치 이력 조회</li>
 *   <li>GET /workers/me/matching-history - 본인 주문 배정 이력 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('WORKER')")
public class WorkerQueryController {

    private final WorkerQueryService workerQueryService;

    /**
     * 본인의 미완료 작업 목록을 조회한다.
     * REJECT, COMPLETE 상태를 제외한 배정 작업을 반환한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 미완료 작업 목록
     */
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getMyPendingTasks(@RequestParam Long employeeId) {
        List<TaskDto> tasks = workerQueryService.getMyPendingTasks(employeeId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * 본인의 상태별 작업 수를 집계하여 반환한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 상태별 작업 수 집계 DTO
     */
    @GetMapping("/tasks/summary")
    public ResponseEntity<ApiResponse<WorkerTaskSummaryDto>> getMyTaskSummary(@RequestParam Long employeeId) {
        WorkerTaskSummaryDto summary = workerQueryService.getMyTaskSummary(employeeId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 본인의 설비 배치 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 설비 배치 이력 목록
     */
    @GetMapping("/workers/me/deployments")
    public ResponseEntity<ApiResponse<List<WorkerDeploymentDto>>> getMyDeployments(
            @RequestParam Long employeeId) {
        List<WorkerDeploymentDto> deployments = workerQueryService.getMyDeployments(employeeId);
        return ResponseEntity.ok(ApiResponse.success(deployments));
    }

    /**
     * 본인의 주문 배정 이력을 조회한다.
     *
     * @param employeeId 조회할 작업자 ID
     * @return 주문 배정 이력 목록
     */
    @GetMapping("/workers/me/matching-history")
    public ResponseEntity<ApiResponse<List<WorkerMatchingHistoryDto>>> getMyMatchingHistory(
            @RequestParam Long employeeId) {
        List<WorkerMatchingHistoryDto> history = workerQueryService.getMyMatchingHistory(employeeId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
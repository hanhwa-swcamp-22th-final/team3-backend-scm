package com.ohgiraffers.team3backendscm.scm.command.application.controller.worker;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.worker.TaskCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 작업자(Worker) 권한의 작업 실행 Command REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>POST /tasks/{taskId}/start         - 작업 시작</li>
 *   <li>POST /tasks/{taskId}/finish-draft  - 작업 종료 임시저장</li>
 *   <li>POST /tasks/{taskId}/finish        - 작업 종료 제출</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class TaskCommandController {

    private final TaskCommandService taskCommandService;

    /**
     * 작업을 시작한다.
     * MatchingRecord 의 workStartAt 을 현재 시각으로 기록한다.
     *
     * @param taskId 시작할 작업의 배정 기록 ID (matching_record_id)
     * @return 성공 여부를 담은 ApiResponse (data = null)
     */
    @PostMapping("/tasks/{taskId}/start")
    public ResponseEntity<ApiResponse<Void>> startTask(@PathVariable Long taskId) {
        taskCommandService.startTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 작업 결과를 임시저장한다.
     * workEndAt·comment 를 기록하되 배정 상태는 유지한다.
     *
     * @param taskId  임시저장할 작업의 배정 기록 ID
     * @param request 코멘트를 담은 요청 DTO
     * @return 성공 여부를 담은 ApiResponse (data = null)
     */
    @PostMapping("/tasks/{taskId}/finish-draft")
    public ResponseEntity<ApiResponse<Void>> finishDraft(
            @PathVariable Long taskId,
            @RequestBody TaskFinishRequest request) {
        taskCommandService.finishDraft(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 작업을 완료 제출한다.
     * MatchingRecord 상태를 COMPLETE 로 전환하고 주문 상태를 COMPLETED 로 변경한다.
     *
     * @param taskId  완료할 작업의 배정 기록 ID
     * @param request 코멘트를 담은 요청 DTO
     * @return 성공 여부를 담은 ApiResponse (data = null)
     */
    @PostMapping("/tasks/{taskId}/finish")
    public ResponseEntity<ApiResponse<Void>> finish(
            @PathVariable Long taskId,
            @RequestBody TaskFinishRequest request) {
        taskCommandService.finish(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

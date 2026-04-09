package com.ohgiraffers.team3backendscm.scm.command.application.controller.worker;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.worker.TaskCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 작업자 권한의 작업 시작, 임시 저장, 종료 제출을 처리하는 Command REST 컨트롤러이다.
 * 기본 경로는 /api/v1/scm 이다.
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class TaskCommandController {

    private final TaskCommandService taskCommandService;

    /**
     * 작업을 시작한다.
     * workStartAt 을 현재 시각으로 기록한다.
     *
     * @param taskId 시작할 작업의 배정 기록 ID
     * @return 성공 응답
     */
    @PostMapping("/workers/me/today-tasks/{taskId}/start")
    public ResponseEntity<ApiResponse<Void>> startTask(@PathVariable Long taskId) {
        taskCommandService.startTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 작업 결과를 임시 저장한다.
     * workEndAt 과 comment 를 저장하되 배정 상태는 유지한다.
     *
     * @param taskId 임시 저장할 작업의 배정 기록 ID
     * @param request 작업 종료 요청 DTO
     * @return 성공 응답
     */
    @PostMapping("/workers/me/today-tasks/{taskId}/finish-draft")
    public ResponseEntity<ApiResponse<Void>> finishDraft(
            @PathVariable Long taskId,
            @RequestBody TaskFinishRequest request) {
        taskCommandService.finishDraft(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 작업 종료를 제출한다.
     * MatchingRecord 상태를 COMPLETE 로 바꾸고 주문 상태를 COMPLETED 로 변경한다.
     *
     * @param taskId 종료 제출할 작업의 배정 기록 ID
     * @param request 작업 종료 요청 DTO
     * @return 성공 응답
     */
    @PostMapping("/workers/me/today-tasks/{taskId}/finish")
    public ResponseEntity<ApiResponse<Void>> finish(
            @PathVariable Long taskId,
            @RequestBody TaskFinishRequest request) {
        taskCommandService.finish(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작업 종료(임시저장/제출) 요청 DTO.
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish-draft
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish
 * 두 엔드포인트에서 공통으로 사용된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskFinishRequest {

    /** 작업 완료 코멘트 (선택 입력) */
    private String comment;
}

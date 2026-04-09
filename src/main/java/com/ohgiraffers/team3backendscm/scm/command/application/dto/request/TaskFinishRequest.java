package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작업 종료 관련 요청 DTO이다.
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish-draft 와
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish 에서 공통으로 사용한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskFinishRequest {

    /** 작업 종료 코멘트 */
    private String comment;
}
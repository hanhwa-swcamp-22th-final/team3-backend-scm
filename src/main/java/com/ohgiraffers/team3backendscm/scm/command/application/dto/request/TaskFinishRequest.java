package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ?묒뾽 醫낅즺(?꾩떆????쒖텧) ?붿껌 DTO.
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish-draft
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish
 * ???붾뱶?ъ씤?몄뿉??怨듯넻?쇰줈 ?ъ슜?쒕떎.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskFinishRequest {

    /** ?묒뾽 ?꾨즺 肄붾찘??(?좏깮 ?낅젰) */
    private String comment;
}

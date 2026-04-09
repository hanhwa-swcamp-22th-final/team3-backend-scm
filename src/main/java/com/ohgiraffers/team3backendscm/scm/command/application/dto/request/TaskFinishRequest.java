package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ?‘ì—… ì¢…ë£Œ(?„ì‹œ?€???œì¶œ) ?”ì²­ DTO.
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish-draft
 * POST /api/v1/scm/workers/me/today-tasks/{taskId}/finish
 * ???”ë“œ?¬ì¸?¸ì—??ê³µí†µ?¼ë¡œ ?¬ìš©?œë‹¤.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskFinishRequest {

    /** ?‘ì—… ?„ë£Œ ì½”ë©˜??(? íƒ ?…ë ¥) */
    private String comment;
}

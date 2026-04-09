package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 작업자 본인의 작업 현황 집계 정보를 담는 응답 DTO.
 * 배정 완료·진행 중·완료 상태의 작업 수를 반환한다.
 * GET /api/v1/scm/tasks/summary 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerTaskSummaryDto {

    private Integer assignedCount;    // 배정 완료(CONFIRM) 상태 작업 수
    private Integer inProgressCount;  // 진행 중(INPROGRESS) 상태 작업 수
    private Integer completedCount;   // 완료(COMPLETE) 상태 작업 수
}

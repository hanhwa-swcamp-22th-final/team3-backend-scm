package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 전체 설비 현황 요약 정보를 담는 응답 DTO.
 * 팀 리더 대시보드에서 설비 상태별 집계 수치를 한눈에 파악할 수 있도록 한다.
 * GET /api/v1/scm/facilities/summary 에서 반환된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySummaryDto {

    private Integer totalCount;           // 전체 설비 수
    private Integer operatingCount;       // 정상 가동 중인 설비 수
    private Integer stoppedCount;         // 가동 중단 설비 수
    private Integer underInspectionCount; // 점검 중인 설비 수
    private Integer disposedCount;        // 폐기된 설비 수
}

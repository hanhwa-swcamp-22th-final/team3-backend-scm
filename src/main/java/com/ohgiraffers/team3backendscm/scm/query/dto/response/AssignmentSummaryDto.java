package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배정 현황 요약 정보를 담는 응답 DTO.
 * 팀 리더 대시보드에서 오늘의 배정 건수, 미배정 주문 수, 배정 정확도를 한눈에 보여준다.
 * GET /api/v1/scm/assignments/summary 에서 반환된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSummaryDto {

    private Integer todayAssignedCount; // 오늘 배정 완료된 주문 수
    private Integer unassignedCount;    // 아직 배정되지 않은 주문 수 (ANALYZED 상태)
    private Double accuracy;            // 배정 정확도 (예: 기대 성과 기준 0.0~1.0)
}

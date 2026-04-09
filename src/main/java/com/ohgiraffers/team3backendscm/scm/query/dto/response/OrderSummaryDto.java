package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 현황 요약 정보를 담는 응답 DTO.
 * 팀 리더 대시보드 상단에 표시되는 전체 주문 수, 진행 건수,
 * 납기 위험 주문 수, 달성률 및 칸반 컬럼별 카운트를 제공한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {

    private Integer totalCount;        // 전체 주문 수
    private Integer inProgressCount;   // 현재 진행 중인 주문 수 (INPROGRESS 상태)
    private Integer deadlineRiskCount; // 납기 위험 주문 수 (납기 3일 이내)
    private Double achievementRate;    // 전체 납기 완료 주문 달성률 (0.0~100.0)
    private Integer registeredCount;   // 등록(REGISTERED) 상태 주문 건수 (칸반 1단계)
    private Integer analyzedCount;     // 분석완료(ANALYZED) 상태 주문 건수 (칸반 2단계)
    private Integer completedCount;    // 완료(COMPLETED) 상태 주문 건수 (칸반 3단계)
}

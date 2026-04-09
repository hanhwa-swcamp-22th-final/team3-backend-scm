package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OCSA 분석 현황 요약 정보를 담는 응답 DTO.
 * 분석 완료된 주문 수, 평균 난이도 점수, 최고 난이도 등급을 반환한다.
 * GET /api/v1/scm/orders/ocsa/summary 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OcsaSummaryDto {

    private Integer analyzedOrderCount; // ANALYZED·INPROGRESS 상태 주문 수 (분석 완료 후 미완료 주문)
    private Double avgDifficultyScore;  // 해당 주문들의 평균 난이도 점수
    private String maxDifficultyGrade;  // 해당 주문들의 최고 난이도 등급 (D1~D5)
}

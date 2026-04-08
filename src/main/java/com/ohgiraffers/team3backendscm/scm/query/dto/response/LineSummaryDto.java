package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공장 라인별 주문 처리 요약 정보를 담는 응답 DTO.
 * 각 라인의 전체 주문 수, 완료 수, 달성률을 제공하여
 * 팀 리더가 라인별 생산 성과를 비교할 수 있도록 한다.
 * GET /api/v1/scm/lines/summary 에서 반환된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineSummaryDto {

    private Long factoryLineId;       // 공장 라인 PK
    private String factoryLineName;   // 공장 라인 명칭
    private Integer totalOrderCount;  // 라인에 할당된 전체 주문 수
    private Integer completedCount;   // 완료된 주문 수
    private Double achievementRate;   // 완료율 (completedCount / totalOrderCount, 0.0~1.0)
}

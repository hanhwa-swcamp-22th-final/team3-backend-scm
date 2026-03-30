package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 라인별 기술자 배치 재조정(Rebalance) 현황을 담는 응답 DTO.
 * 각 공장 라인에 배치된 기술자의 티어 분포와 권장 배치 수를 제공하여
 * 팀 리더가 인력 불균형을 파악하고 재배정 계획을 세우는 데 활용된다.
 * GET /api/v1/scm/assignments/rebalance 에서 반환된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRebalanceDto {

    private Long factoryLineId;         // 공장 라인 ID
    private String factoryLineName;     // 공장 라인 명칭
    private Integer totalTechnicianCount; // 라인에 배치된 기술자 총 수
    private Integer tierSCount;         // S 티어 기술자 수
    private Integer tierACount;         // A 티어 기술자 수
    private Integer tierBCount;         // B 티어 기술자 수
    private Integer tierCCount;         // C 티어 기술자 수
    private Integer recommendedCount;   // 시스템 권장 배치 인원 수
}

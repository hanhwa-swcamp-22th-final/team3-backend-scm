package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 배정 후보 기술자 정보를 담는 응답 DTO.
 * 팀 리더가 주문을 배정할 기술자를 선택할 때 제공하는 정보 목록을 담고 있으며,
 * 보유 티어, OCSA 점수, 해당 주문에 대한 적합도 점수를 포함한다.
 * GET /api/v1/scm/assignments/candidates 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentCandidateDto {

    private Long employeeId;          // 기술자(직원) ID
    private String employeeName;      // 기술자 이름
    private String tier;              // 보유 티어 (S / A / B / C)
    private BigDecimal score;         // 기술자의 OCSA 평가 점수
    private BigDecimal suitabilityScore; // 해당 주문에 대한 적합도 점수
    private String matchingMode;      // 선택 주문 기준 배정 방식 (EFFICIENCY_TYPE / GROWTH_TYPE)
}

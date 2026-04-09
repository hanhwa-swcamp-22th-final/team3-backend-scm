package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 특정 주문의 OCSA 난이도 분석 상세 결과를 담는 응답 DTO.
 * V1~V4 지표 및 신규성(α) 보정 계수, 최종 점수·등급, 추천 기술자 ID를 제공한다.
 * GET /api/v1/scm/orders/{orderId}/ocsa 엔드포인트에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderOcsaDto {

    private Long orderId;                       // 주문 PK
    private BigDecimal v1ProcessComplexity;     // V1: 공정 복잡도 점수
    private BigDecimal v2QualityPrecision;      // V2: 품질 정밀도 점수
    private BigDecimal v3CapacityRequirements;  // V3: 설비 역량 요구도 점수
    private BigDecimal v4SpaceTimeUrgency;      // V4: 공간·시간 긴급도 점수
    private BigDecimal alphaNovlety;            // α: 신규성 보정 계수
    private BigDecimal difficultyScore;         // OCSA 가중합으로 산출된 최종 난이도 점수
    private DifficultyGrade difficultyGrade;    // 난이도 등급 (D1~D5)
    private Long recommendedTechnicianId;       // 시스템이 추천한 기술자 ID (null 가능)
}

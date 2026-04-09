package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 배정 상세 조회 응답 DTO.
 * GET /api/v1/scm/assignments/{matchingRecordId} 에서 반환한다.
 * matching_record, orders, employee 테이블을 JOIN하여 구성한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDetailDto {

    private Long   matchingRecordId;  // 배정 기록 PK

    // 대상 주문 정보 섹션
    private Long   orderId;           // 배정 대상 주문 ID
    private String orderNo;           // 주문 번호
    private String orderStatus;       // 주문 현재 상태

    // 대상 기술자 정보 섹션
    private Long   employeeId;        // 배정된 기술자 ID
    private String employeeName;      // 기술자 이름
    private String tier;              // 기술자의 보유 등급 (S/A/B/C)

    // 대상 배정 정보 섹션
    private String matchingMode;      // 배정 방식 (GROWTH_TYPE / EFFICIENCY_TYPE)
    private String matchingStatus;    // 배정 처리 상태

    // 대상 결과 지표 섹션
    private BigDecimal dcRatio;              // 난이도 대비 숙련도 비율
    private BigDecimal expectedBonus;        // 기대 보너스
    private BigDecimal expectedProductivity; // 기대 생산성
    private BigDecimal qualityRisk;          // 품질 리스크

    // 대상 작업 진행 정보 섹션
    private LocalDateTime workStartAt;  // 작업 시작 일시
    private LocalDateTime workEndAt;    // 작업 종료 일시
    private String        comment;      // 작업 코멘트

    private LocalDateTime assignedAt;   // 배정 생성 일시 (created_at)
}

package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 배정 상세 조회 응답 DTO.
 * GET /api/v1/scm/assignments/{matchingRecordId} 에서 반환된다.
 * matching_record, orders, employee 테이블을 JOIN하여 구성한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDetailDto {

    private Long   matchingRecordId;  // 배정 기록 PK

    // ── 주문 정보 ──
    private Long   orderId;           // 배정 대상 주문 ID
    private String orderNo;           // 주문 번호
    private String orderStatus;       // 주문 현재 상태

    // ── 기술자 정보 ──
    private Long   employeeId;        // 배정된 기술자 ID
    private String employeeName;      // 기술자 이름
    private String tier;              // 기술자 역량 등급 (S/A/B/C)

    // ── 배정 정보 ──
    private String matchingMode;      // 배정 방식 (GROWTH_TYPE / EFFICIENCY_TYPE)
    private String matchingStatus;    // 배정 처리 상태

    // ── 성과 지표 ──
    private BigDecimal dcRatio;              // 난이도 대비 역량 비율
    private BigDecimal expectedBonus;        // 기대 보너스
    private BigDecimal expectedProductivity; // 기대 생산성
    private BigDecimal qualityRisk;          // 품질 리스크

    // ── 작업 실행 정보 ──
    private LocalDateTime workStartAt;  // 작업 시작 일시
    private LocalDateTime workEndAt;    // 작업 종료 일시
    private String        comment;      // 작업 코멘트

    private LocalDateTime assignedAt;   // 배정 생성 일시 (created_at)
}

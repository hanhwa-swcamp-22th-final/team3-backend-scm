package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 작업자(Worker) 미완료 작업 목록 조회 응답 DTO.
 * GET /api/v1/scm/tasks 에서 반환된다.
 * matching_record, orders, product 테이블을 JOIN하여 구성한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long   taskId;          // 작업 ID (matching_record_id)

    // ── 주문 정보 ──
    private Long      orderId;        // 주문 ID
    private String    orderNo;        // 주문 번호
    private String    productName;    // 제품명
    private String    difficultyGrade; // 작업 난이도 (D1~D5)
    private LocalDate dueDate;        // 납기 마감일

    // ── 배정 정보 ──
    private String matchingMode;    // 배정 방식 (GROWTH_TYPE / EFFICIENCY_TYPE)
    private String matchingStatus;  // 배정 처리 상태

    // ── 작업 실행 정보 ──
    private LocalDateTime workStartAt;  // 작업 시작 일시 (null이면 미시작)
    private LocalDateTime workEndAt;    // 작업 종료 일시 (null이면 미완료)
    private String        comment;      // 작업 코멘트
}

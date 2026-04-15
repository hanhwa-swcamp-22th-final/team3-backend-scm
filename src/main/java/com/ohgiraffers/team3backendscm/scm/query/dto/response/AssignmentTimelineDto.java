package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 라인별 배정 타임라인 정보를 담는 응답 DTO.
 * 특정 공장 라인의 어떤 기술자가 실제 배정되었고 해당 주문의 상태가 어떤지를 시계열로 보여준다.
 * GET /api/v1/scm/assignments/timeline 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTimelineDto {

    private Long factoryLineId;      // 공장 라인 ID
    private String factoryLineName;  // 공장 라인 명칭
    private Long employeeId;         // 배정된 기술자(직원) ID
    private String employeeName;     // 배정된 기술자 이름
    private String employeeTier;     // 배정된 기술자 티어
    private LocalDate assignedDate;  // 배정 날짜
    private String matchingStatus;   // 배정 처리 상태 (MatchingStatus 문자열)
    private String orderNo;          // 관련 주문 번호
    private String orderStatus;      // 관련 주문 처리 상태 (OrderStatus 문자열)
    private LocalDateTime workStartAt; // 작업 시작 시각
    private LocalDateTime workEndAt;   // 작업 종료 시각
}

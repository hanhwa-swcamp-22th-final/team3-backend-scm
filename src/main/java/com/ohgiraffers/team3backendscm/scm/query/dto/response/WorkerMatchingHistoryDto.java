package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 작업자(Worker) 본인의 주문 배정 이력을 담는 응답 DTO.
 * 작업자가 자신이 어떤 주문에 실제 배정되었고 처리 상태가 어떤지 조회할 때 사용한다.
 * GET /api/v1/scm/workers/me/matching-history 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerMatchingHistoryDto {

    private Long matchingRecordId; // 배정 기록(MatchingRecord) PK
    private Long orderId;          // 배정된 주문 ID
    private String orderNumber;    // 주문 번호
    private LocalDate assignedDate; // 배정 날짜
    private String status;         // 배정 처리 상태 (MatchingStatus 문자열)
}


package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 설비 이벤트 이력 정보를 담는 응답 DTO.
 * 설비에서 발생한 장애, 점검, 교체 등의 이벤트 내역을 시간순으로 제공한다.
 * GET /api/v1/scm/facilities/{facilityId}/history 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityHistoryDto {

    private Long eventId;              // 이벤트 PK
    private String eventType;          // 이벤트 유형 (예: FAULT, MAINTENANCE, REPLACEMENT)
    private LocalDateTime occurredAt;  // 이벤트 발생 일시
    private String description;        // 이벤트 상세 설명
}

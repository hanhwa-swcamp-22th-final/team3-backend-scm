package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 설비(Facility/Equipment) 기본 정보를 담는 응답 DTO.
 * 설비 ID, 명칭, 운영 상태, 분류, 해당 관리자 ID를 제공한다.
 * GET /api/v1/scm/facilities 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {

    private Long equipmentId;      // 설비(Equipment) PK
    private String equipmentName;  // 설비 명칭
    private String status;         // 운영 상태 (예: OPERATING, STOPPED, INSPECTION, DISPOSED)
    private String category;       // 설비 분류 (예: 도장, 조립, 검수 등)
    private Long managerId;        // 해당 관리자(employee_id)
}

package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 특정 설비에 배치된 기술자(직원) 배치 정보를 담는 응답 DTO.
 * 누가 언제 어떤 날짜에 해당 설비에 배치되었는지를 제공한다.
 * GET /api/v1/scm/facilities/{facilityId}/deployments 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDeploymentDto {

    private Long employeeId;          // 배치된 직원(기술자) ID
    private String employeeName;      // 배치된 직원 이름
    private LocalDate deploymentDate; // 배치 날짜
}

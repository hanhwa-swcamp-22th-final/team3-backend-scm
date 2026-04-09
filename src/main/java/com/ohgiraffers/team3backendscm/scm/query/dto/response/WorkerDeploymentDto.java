package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 작업자(Worker) 본인의 설비 배치 이력을 담는 응답 DTO.
 * 작업자가 자신이 어떤 설비에 실제 배치되었고 어떤 역할을 수행했는지 조회할 때 사용한다.
 * GET /api/v1/scm/workers/me/deployments 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDeploymentDto {

    private Long facilityId;          // 배치된 설비(facility) ID
    private String facilityName;      // 설비 명칭
    private LocalDate deploymentDate; // 배치 날짜
    private String role;              // 해당 배치에서의 역할 (예: 안전, 점검, 보조 등)
}

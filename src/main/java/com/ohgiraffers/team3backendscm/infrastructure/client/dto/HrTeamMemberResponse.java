package com.ohgiraffers.team3backendscm.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * HR 서비스의 팀리더 대시보드 팀원 조회 응답 중 SCM에서 필요한 필드만 추출한 DTO.
 * GET /api/v1/hr/team-leader/dashboard/members 에서 반환되는 항목과 매핑한다.
 */
@Getter
@NoArgsConstructor
public class HrTeamMemberResponse {

    private Long employeeId;
    private String employeeName;
    private String tier;
}

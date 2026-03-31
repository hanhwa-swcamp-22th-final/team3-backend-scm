package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배정 기술자 변경(재배정) 요청 DTO.
 * PUT /api/v1/scm/assignments/{matchingRecordId} 에서 사용된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReassignRequest {

    /** 새로 배정할 기술자(employee_id) */
    private Long technicianId;
}

package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배정된 기술자를 변경할 때 사용하는 요청 DTO이다.
 * PUT /api/v1/scm/assignments/{matchingRecordId} 에서 사용한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReassignRequest {

    /** 새로 배정할 기술자 employee_id */
    private Long technicianId;
}
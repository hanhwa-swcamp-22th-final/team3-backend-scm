package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 특정 공장 라인에 배치된 작업자 정보를 담는 응답 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineWorkerDto {

    private Long factoryLineId;
    private Long employeeId;
    private String employeeName;
    private String employeeTier;
    private Long equipmentId;
    private String equipmentName;
}

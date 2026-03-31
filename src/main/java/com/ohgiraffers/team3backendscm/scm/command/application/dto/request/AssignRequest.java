package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀 리더가 기술자를 주문에 배정할 때 사용하는 요청 DTO.
 * POST /api/v1/scm/assignments 엔드포인트의 요청 바디로 전달된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequest {

    private Long orderId;      // 배정 대상 주문 ID
    private Long technicianId; // 배정할 기술자(employee_id)
}

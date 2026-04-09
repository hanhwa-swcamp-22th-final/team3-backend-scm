package com.ohgiraffers.team3backendscm.scm.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문에 기술자를 최초 배정할 때 사용하는 요청 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequest {

    private Long orderId;      // 배정 대상 주문 ID
    private Long technicianId; // 배정할 기술자(employee_id)
}

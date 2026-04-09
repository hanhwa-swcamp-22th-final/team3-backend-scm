package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 기술자(Technician) 정보를 담는 응답 DTO.
 * 팀 리더가 배정 가능한 기술자 목록을 조회할 때 사용하며,
 * 숙련도 티어, OCSA 점수, 특정 주문에 대한 적합도를 포함한다.
 * GET /api/v1/scm/technicians 에서 반환한다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDto {

    private Long employeeId;       // 기술자(직원) ID
    private String employeeName;   // 기술자 이름
    private String tier;           // 숙련도 티어 (S / A / B / C)
    private BigDecimal ocsaScore;  // 기술자의 OCSA 점수
    private BigDecimal suitability; // 해당 주문에 대한 적합도 점수 (0.0~1.0)
}


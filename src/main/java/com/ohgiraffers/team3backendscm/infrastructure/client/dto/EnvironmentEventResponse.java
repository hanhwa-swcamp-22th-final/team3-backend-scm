package com.ohgiraffers.team3backendscm.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Admin 모듈의 GET /api/v1/equipment-management/environment-events?mode=history 응답 DTO.
 */
@Getter
@NoArgsConstructor
public class EnvironmentEventResponse {

    private Long equipmentId;
    private BigDecimal envTemperature;
    private BigDecimal envHumidity;
    private Integer envParticleCnt;
    private String envDeviationType;
    private LocalDateTime envDetectedAt;
}

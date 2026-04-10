package com.ohgiraffers.team3backendscm.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Admin 모듈의 GET /api/v1/equipment-management/equipments?mode=summary 응답 DTO.
 */
@Getter
@NoArgsConstructor
public class EquipmentSummaryResponse {

    private long totalCount;
    private long operatingCount;
    private long stoppedCount;
    private long underInspectionCount;
    private long disposedCount;
}

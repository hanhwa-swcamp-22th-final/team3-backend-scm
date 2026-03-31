package com.ohgiraffers.team3backendscm.scm.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 설비 환경 이상 감지 트렌드 데이터를 담는 응답 DTO.
 * 특정 설비에서 감지된 온도, 습도, 파티클 수치 이상 이벤트를 시계열로 제공하여
 * 설비 상태 모니터링 및 이상 패턴 분석에 사용된다.
 * GET /api/v1/scm/facilities/{facilityId}/trends 에서 반환된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityTrendsDto {

    private Long equipmentId;          // 설비(장비) PK
    private LocalDateTime detectedAt;  // 이상 감지 일시
    private BigDecimal temperature;    // 감지 당시 온도 (℃)
    private BigDecimal humidity;       // 감지 당시 습도 (%)
    private Integer particleCnt;       // 감지 당시 파티클(먼지) 수
    private String deviationType;      // 이상 유형 (예: TEMP_HIGH, HUMID_LOW, PARTICLE_HIGH)
}

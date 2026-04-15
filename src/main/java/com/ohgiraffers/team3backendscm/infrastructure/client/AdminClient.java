package com.ohgiraffers.team3backendscm.infrastructure.client;

import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EnvironmentEventResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EquipmentSummaryResponse;

import java.util.List;

/**
 * Admin 모듈 클라이언트 계약 인터페이스.
 * HTTP/Feign 세부 구현을 숨기고 서비스 레이어에 순수 비즈니스 메서드만 노출한다.
 */
public interface AdminClient {

    /**
     * 직원 프로필을 조회한다.
     */
    AdminEmployeeProfileResponse getEmployeeProfile(Long employeeId);

    /**
     * 특정 티어의 활성 작업자 ID 목록을 조회한다.
     */
    List<Long> getActiveWorkerIdsByTier(String tier);

    /**
     * 설비 상태별 집계 요약을 조회한다.
     */
    EquipmentSummaryResponse getEquipmentSummary();

    /**
     * 특정 설비의 환경 이벤트 이력을 조회한다.
     *
     * @param equipmentId 조회할 설비 ID (null 이면 전체)
     */
    List<EnvironmentEventResponse> getEnvironmentEvents(Long equipmentId);
}

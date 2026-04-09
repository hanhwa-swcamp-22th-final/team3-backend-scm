package com.ohgiraffers.team3backendscm.infrastructure.client;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EnvironmentEventResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EquipmentSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Admin 모듈 Feign Client.
 * 설비 요약 및 환경 이벤트 데이터를 Admin REST API를 통해 조회한다.
 */
@FeignClient(name = "admin-client", url = "${admin.url}")
public interface AdminClient {

    /**
     * 설비 상태별 집계 요약 조회.
     * Admin: GET /api/v1/equipment-management/equipments?mode=summary
     */
    @GetMapping("/api/v1/equipment-management/equipments")
    ApiResponse<EquipmentSummaryResponse> getEquipmentSummary(
            @RequestParam("mode") String mode
    );

    /**
     * 특정 설비의 환경 이벤트 이력 조회.
     * Admin: GET /api/v1/equipment-management/environment-events?mode=history&equipmentId={id}
     */
    @GetMapping("/api/v1/equipment-management/environment-events")
    ApiResponse<List<EnvironmentEventResponse>> getEnvironmentEvents(
            @RequestParam("mode") String mode,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId
    );
}

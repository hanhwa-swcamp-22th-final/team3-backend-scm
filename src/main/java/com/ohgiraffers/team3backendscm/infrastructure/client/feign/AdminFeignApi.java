package com.ohgiraffers.team3backendscm.infrastructure.client.feign;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EnvironmentEventResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EquipmentSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Admin 모듈 Feign Client.
 * HTTP 매핑과 JWT 전달만 담당하며, 응답 언래핑은 AdminFeignClient 에서 처리한다.
 */
@FeignClient(name = "admin-feign-api", url = "${admin.url}", configuration = AdminFeignConfiguration.class)
public interface AdminFeignApi {

    @GetMapping("/api/v1/equipment-management/equipments")
    ApiResponse<EquipmentSummaryResponse> getEquipmentSummary(
            @RequestParam("mode") String mode
    );

    @GetMapping("/api/v1/equipment-management/environment-events")
    ApiResponse<List<EnvironmentEventResponse>> getEnvironmentEvents(
            @RequestParam("mode") String mode,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId
    );
}

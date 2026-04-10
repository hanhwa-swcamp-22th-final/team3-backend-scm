package com.ohgiraffers.team3backendscm.infrastructure.client;

import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EnvironmentEventResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EquipmentSummaryResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.feign.AdminFeignApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AdminClient Feign 구현체.
 * AdminFeignApi 를 호출하고 ApiResponse 를 언래핑해 서비스 레이어에 도메인 객체만 반환한다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeignClient implements AdminClient {

    private final AdminFeignApi adminFeignApi;

    @Override
    public EquipmentSummaryResponse getEquipmentSummary() {
        var response = adminFeignApi.getEquipmentSummary("summary");
        return response == null ? null : response.getData();
    }

    @Override
    public List<EnvironmentEventResponse> getEnvironmentEvents(Long equipmentId) {
        var response = adminFeignApi.getEnvironmentEvents("history", equipmentId);
        if (response == null || response.getData() == null) {
            return List.of();
        }
        return response.getData();
    }
}

package com.ohgiraffers.team3backendscm.infrastructure.client;

import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.feign.HrFeignApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * HrClient Feign 구현체.
 * HrFeignApi 를 호출하고 ApiResponse 를 언래핑해 서비스 레이어에 도메인 객체만 반환한다.
 */
@Component
@RequiredArgsConstructor
public class HrFeignClient implements HrClient {

    private final HrFeignApi hrFeignApi;

    @Override
    public List<HrTeamMemberResponse> getTeamMembers() {
        var response = hrFeignApi.getTeamMembers(0, 200);
        if (response == null || response.getData() == null) {
            return List.of();
        }
        return response.getData();
    }
}

package com.ohgiraffers.team3backendscm.infrastructure.client.feign;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.HrTeamMemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * HR 모듈 Feign Client.
 * JWT 전달만 담당하며, 응답 언래핑은 HrClient 에서 처리한다.
 */
@FeignClient(name = "hr-feign-api", url = "${hr.url}", configuration = HrFeignConfiguration.class)
public interface HrFeignApi {

    @GetMapping("/api/v1/hr/team-leader/dashboard/members")
    ApiResponse<List<HrTeamMemberResponse>> getTeamMembers(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}

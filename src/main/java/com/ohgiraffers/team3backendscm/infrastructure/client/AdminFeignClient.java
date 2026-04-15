package com.ohgiraffers.team3backendscm.infrastructure.client;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "adminFeignClient",
        url = "${admin.url}",
        configuration = AdminFeignConfiguration.class
)
public interface AdminFeignClient {

    @GetMapping("/api/v1/admin/employees/{employeeId}/profile")
    ApiResponse<AdminEmployeeProfileResponse> getEmployeeProfile(@PathVariable("employeeId") Long employeeId);

    @GetMapping("/api/v1/admin/employees/{leaderId}/team-members")
    ApiResponse<List<Long>> getTeamMemberIds(@PathVariable("leaderId") Long leaderId);

    @GetMapping("/api/v1/admin/employees/workers/active")
    ApiResponse<List<Long>> getActiveWorkerIdsByTier(@RequestParam("tier") String tier);
}

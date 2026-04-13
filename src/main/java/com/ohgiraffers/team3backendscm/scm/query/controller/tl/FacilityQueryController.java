package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityTrendsDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.FacilityQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 설비 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>GET /facilities - 전체 설비 목록 조회</li>
 *   <li>GET /facilities/{facilityId}/history - 특정 설비 이력 조회</li>
 *   <li>GET /facilities/{facilityId}/deployments - 특정 설비 배치 인원 조회</li>
 *   <li>GET /facilities/summary - 전체 설비 운영 요약 조회</li>
 *   <li>GET /facilities/{facilityId}/trends - 특정 설비 환경 이상 트렌드 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TL')")
public class FacilityQueryController {

    private final FacilityQueryService facilityQueryService;

    /**
     * 전체 설비 목록을 조회한다.
     *
     * @return 설비 기본 정보 목록
     */
    @GetMapping("/facilities")
    public ResponseEntity<ApiResponse<List<FacilityDto>>> getFacilities() {
        List<FacilityDto> facilities = facilityQueryService.getFacilities();
        return ResponseEntity.ok(ApiResponse.success(facilities));
    }

    /**
     * 특정 설비의 이력 정보를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 이력 정보 목록
     */
    @GetMapping("/facilities/{facilityId}/history")
    public ResponseEntity<ApiResponse<List<FacilityHistoryDto>>> getFacilityHistory(
            @PathVariable Long facilityId) {
        List<FacilityHistoryDto> history = facilityQueryService.getFacilityHistory(facilityId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 특정 설비에 배치된 기술자 정보를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 배치 인원 목록
     */
    @GetMapping("/facilities/{facilityId}/deployments")
    public ResponseEntity<ApiResponse<List<FacilityDeploymentDto>>> getFacilityDeployments(
            @PathVariable Long facilityId) {
        List<FacilityDeploymentDto> deployments = facilityQueryService.getFacilityDeployments(facilityId);
        return ResponseEntity.ok(ApiResponse.success(deployments));
    }

    /**
     * 로그인한 TL의 팀원이 배치된 설비 목록을 조회한다.
     *
     * @return 팀원 배치 설비 목록
     */
    @GetMapping("/facilities/my-team")
    public ResponseEntity<ApiResponse<List<FacilityDto>>> getMyTeamFacilities() {
        List<FacilityDto> facilities = facilityQueryService.getMyTeamFacilities();
        return ResponseEntity.ok(ApiResponse.success(facilities));
    }

    /**
     * 전체 설비 운영 요약을 조회한다.
     *
     * @return 설비 운영 요약 DTO
     */
    @GetMapping("/facilities/summary")
    public ResponseEntity<ApiResponse<FacilitySummaryDto>> getFacilitySummary() {
        FacilitySummaryDto summary = facilityQueryService.getFacilitySummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 특정 설비의 환경 이상 트렌드 데이터를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 환경 이상 트렌드 목록
     */
    @GetMapping("/facilities/{facilityId}/trends")
    public ResponseEntity<ApiResponse<List<FacilityTrendsDto>>> getFacilityTrends(
            @PathVariable Long facilityId) {
        List<FacilityTrendsDto> trends = facilityQueryService.getFacilityTrends(facilityId);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
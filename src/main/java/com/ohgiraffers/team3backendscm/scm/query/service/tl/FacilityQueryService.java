package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.AdminClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EnvironmentEventResponse;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.EquipmentSummaryResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityTrendsDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.FacilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 설비 조회 Query 서비스.
 * 설비 목록·이력·배치 인원은 SCM DB(FacilityMapper)에서 조회하고,
 * 설비 요약·환경 트렌드는 Admin Feign Client를 통해 조회한다.
 */
@Service
@RequiredArgsConstructor
public class FacilityQueryService {

    private final FacilityMapper facilityMapper;
    private final AdminClient adminClient;

    /**
     * 전체 설비 목록을 조회한다.
     *
     * @return 설비 기본 정보 목록
     */
    public List<FacilityDto> getFacilities() {
        return facilityMapper.findFacilities();
    }

    /**
     * 특정 설비의 이력 정보를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 이력 정보 목록
     */
    public List<FacilityHistoryDto> getFacilityHistory(Long facilityId) {
        return facilityMapper.findFacilityHistory(facilityId);
    }

    /**
     * 특정 설비에 배치된 기술자 정보를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 배치 인원 목록
     */
    public List<FacilityDeploymentDto> getFacilityDeployments(Long facilityId) {
        return facilityMapper.findFacilityDeployments(facilityId);
    }

    /**
     * 전체 설비 운영 요약을 조회한다.
     * Admin의 GET /api/v1/equipment-management/equipments?mode=summary 를 호출한다.
     *
     * @return 설비 운영 요약 DTO
     */
    public FacilitySummaryDto getFacilitySummary() {
        EquipmentSummaryResponse res = adminClient.getEquipmentSummary();
        return new FacilitySummaryDto(
                (int) res.getTotalCount(),
                (int) res.getOperatingCount(),
                (int) res.getStoppedCount(),
                (int) res.getUnderInspectionCount(),
                (int) res.getDisposedCount()
        );
    }

    /**
     * 특정 설비의 환경 이상 트렌드 정보를 조회한다.
     * Admin의 GET /api/v1/equipment-management/environment-events?mode=history&equipmentId={id} 를 호출한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 환경 이상 트렌드 목록
     */
    public List<FacilityTrendsDto> getFacilityTrends(Long facilityId) {
        List<EnvironmentEventResponse> events = adminClient.getEnvironmentEvents(facilityId);
        return events.stream()
                .map(e -> new FacilityTrendsDto(
                        e.getEquipmentId(),
                        e.getEnvDetectedAt(),
                        e.getEnvTemperature(),
                        e.getEnvHumidity(),
                        e.getEnvParticleCnt(),
                        e.getEnvDeviationType()
                ))
                .toList();
    }
}
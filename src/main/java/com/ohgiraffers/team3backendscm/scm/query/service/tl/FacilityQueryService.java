package com.ohgiraffers.team3backendscm.scm.query.service.tl;

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
 * FacilityMapper를 통해 설비 목록, 이력, 배치 인원, 요약, 트렌드 데이터를 읽기 전용으로 제공한다.
 */
@Service
@RequiredArgsConstructor
public class FacilityQueryService {

    private final FacilityMapper facilityMapper;

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
     *
     * @return 설비 운영 요약 DTO
     */
    public FacilitySummaryDto getFacilitySummary() {
        return facilityMapper.findFacilitySummary();
    }

    /**
     * 특정 설비의 환경 이상 트렌드 정보를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 환경 이상 트렌드 목록
     */
    public List<FacilityTrendsDto> getFacilityTrends(Long facilityId) {
        return facilityMapper.findFacilityTrends(facilityId);
    }
}
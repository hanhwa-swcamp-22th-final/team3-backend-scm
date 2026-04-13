package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilitySummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.FacilityTrendsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 설비(Facility/Equipment) 조회 전용 MyBatis 매퍼 인터페이스.
 * 설비 목록, 이벤트 이력, 배치 인원, 요약 집계, 환경 트렌드 데이터 조회를 담당한다.
 * SQL은 src/main/resources/mapper/facilities.xml 에 정의한다.
 */
@Mapper
public interface FacilityMapper {

    /**
     * 전체 설비 목록을 조회한다.
     *
     * @return 설비 기본 정보 목록
     */
    List<FacilityDto> findFacilities();

    /**
     * 주어진 팀원 ID 목록에 속한 직원이 현재 배치된 설비 목록을 조회한다.
     *
     * @param employeeIds 팀원 employee_id 목록
     * @return 팀원 배치 설비 목록
     */
    List<FacilityDto> findTeamFacilities(@Param("employeeIds") List<Long> employeeIds);

    /**
     * 특정 설비의 이벤트 이력(장애, 점검, 교체 등)을 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 이벤트 이력 목록
     */
    List<FacilityHistoryDto> findFacilityHistory(Long facilityId);

    /**
     * 특정 설비에 배치된 기술자(직원) 정보를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 배치 인원 목록
     */
    List<FacilityDeploymentDto> findFacilityDeployments(Long facilityId);

    /**
     * 전체 설비 상태별 집계 요약(가동, 중단, 점검, 폐기)을 조회한다.
     *
     * @return 설비 현황 요약 DTO
     */
    FacilitySummaryDto findFacilitySummary();

    /**
     * 특정 설비의 환경 이상 감지 트렌드 데이터(온도, 습도, 파티클)를 조회한다.
     *
     * @param facilityId 조회할 설비 ID
     * @return 환경 트렌드 데이터 목록
     */
    List<FacilityTrendsDto> findFacilityTrends(Long facilityId);
}

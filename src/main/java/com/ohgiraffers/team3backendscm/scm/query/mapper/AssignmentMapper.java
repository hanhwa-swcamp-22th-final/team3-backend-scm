package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentCandidateDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentTimelineDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 배정(Assignment) 조회 전용 MyBatis 매퍼 인터페이스.
 * SQL은 src/main/resources/mapper/assignments.xml 에 정의한다.
 * 후보 기술자, 배정 요약, 타임라인, 재조정 현황 등의 조회 기능을 제공한다.
 */
@Mapper
public interface AssignmentMapper {

    /**
     * 배정 기록 ID로 단건 배정 상세를 조회한다.
     * matching_record, orders, employee 를 JOIN하여 반환한다.
     *
     * @param matchingRecordId 조회할 배정 기록 PK
     * @return 배정 상세 DTO (존재하지 않으면 empty)
     */
    Optional<AssignmentDetailDto> findById(Long matchingRecordId);

    /**
     * 배정 가능한 기술자 정보 목록을 조회한다.
     * 각 후보의 숙련도 티어, OCSA 점수, 적합도를 포함한다.
     *
     * @return 배정 후보 기술자 목록
     */
    List<AssignmentCandidateDto> findCandidates();

    /**
     * 지정된 팀원만 배정 후보 기술자로 조회한다.
     *
     * @param employeeIds 현재 TL 소속 팀원 employee_id 목록
     * @return 배정 후보 기술자 목록
     */
    List<AssignmentCandidateDto> findCandidatesByEmployeeIds(
            @Param("employeeIds") List<Long> employeeIds,
            @Param("orderId") Long orderId
    );

    /**
     * 오늘 배정 수, 미배정 주문 수, 배정 정확도 등 배정 현황 요약을 조회한다.
     *
     * @return 배정 현황 요약 DTO
     */
    AssignmentSummaryDto findSummary();

    /**
     * 라인별 배정 타임라인(기술자, 날짜, 주문 상태)을 조회한다.
     *
     * @return 배정 타임라인 목록
     */
    List<AssignmentTimelineDto> findTimeline();

    /**
     * 라인별 기술자 티어 분포 및 권장 배치 인원 포함 재조정 현황을 조회한다.
     *
     * @return 라인별 재조정 현황 목록
     */
    List<AssignmentRebalanceDto> findRebalance();
}

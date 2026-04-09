package com.ohgiraffers.team3backendscm.scm.query.mapper;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 공장 라인(Line) 조회 전용 MyBatis 매퍼 인터페이스.
 * 라인별 주문 처리 요약 및 특정 라인의 실시간 운영 현황 조회를 담당한다.
 * SQL은 src/main/resources/mapper/lines.xml 에 정의한다.
 */
@Mapper
public interface LineMapper {

    /**
     * 전체 공장 라인의 주문 처리 요약(총 주문 수, 완료 수, 달성률)을 조회한다.
     *
     * @return 라인별 요약 목록
     */
    List<LineSummaryDto> findLinesSummary();

    /**
     * 특정 라인의 실시간 운영 현황(배정 기술자 수, 진행 주문 수, 설비 가동률)을 조회한다.
     *
     * @param lineId 조회할 라인 ID
     * @return 라인 운영 현황 DTO (없으면 null)
     */
    LineStatusDto findLineStatus(Long lineId);
}

package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.LineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 공장 라인 조회 Query 서비스.
 * LineMapper를 통해 라인별 주문 요약과 개별 라인 운영 현황을 읽기 전용으로 제공한다.
 */
@Service
@RequiredArgsConstructor
public class LineQueryService {

    private final LineMapper lineMapper;

    /**
     * 전체 공장 라인의 주문 처리 요약을 조회한다.
     *
     * @return 라인별 요약 목록
     */
    public List<LineSummaryDto> getLinesSummary() {
        return lineMapper.findLinesSummary();
    }

    /**
     * 특정 라인의 실시간 운영 현황을 조회한다.
     *
     * @param lineId 조회할 라인 ID
     * @return 라인 운영 현황 DTO
     */
    public LineStatusDto getLineStatus(Long lineId) {
        return lineMapper.findLineStatus(lineId);
    }
}
package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.LineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 공장 라인 조회 Query 서비스.
 * LineMapper를 통해 라인별 주문 요약과 개별 라인 운영 현황을 읽기 전용으로 제공한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LineQueryService {

    private final LineMapper lineMapper;
    private final HrClient hrClient;

    /**
     * 전체 공장 라인의 주문 처리 요약을 조회한다.
     *
     * @return 라인별 요약 목록
     */
    public List<LineSummaryDto> getLinesSummary() {
        return lineMapper.findLinesSummary();
    }

    /**
     * 현재 인증된 팀리더의 팀원이 배치된 라인 요약을 조회한다.
     * HR 서비스에서 팀원 목록을 가져온 뒤 해당 직원이 배치된 라인만 반환한다.
     * HR 호출에 실패하면 전체 라인 목록으로 폴백한다.
     */
    public List<LineSummaryDto> getMyTeamLinesSummary() {
        try {
            List<Long> employeeIds = hrClient.getTeamMembers().stream()
                    .map(m -> m.getEmployeeId())
                    .toList();
            if (employeeIds.isEmpty()) {
                return List.of();
            }
            return lineMapper.findTeamLinesSummary(employeeIds);
        } catch (Exception e) {
            log.warn("HR 팀원 조회 실패, 전체 라인으로 폴백: {}", e.getMessage());
            return lineMapper.findLinesSummary();
        }
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
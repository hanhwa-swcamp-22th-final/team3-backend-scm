package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentCandidateDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentTimelineDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.AssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 팀 리더(TL) 권한의 배정 조회 Query 서비스.
 * AssignmentMapper를 통해 후보 기술자, 배정 요약, 타임라인, 재조정 현황 등을 읽기 전용으로 제공한다.
 */
@Service
@RequiredArgsConstructor
public class AssignmentQueryService {

    private final AssignmentMapper assignmentMapper;
    private final HrClient hrClient;

    /**
     * 배정 기록 ID로 배정 상세 정보를 조회한다.
     *
     * @param matchingRecordId 조회할 배정 기록 PK
     * @return 배정 상세 DTO
     * @throws NoSuchElementException 배정 기록이 존재하지 않을 경우
     */
    public AssignmentDetailDto getAssignment(Long matchingRecordId) {
        return assignmentMapper.findById(matchingRecordId)
                .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + matchingRecordId));
    }

    /**
     * 배정 가능한 기술자 정보 목록을 조회한다.
     *
     * @return 후보 기술자 목록 (보유 티어, OCSA 점수, 적합도 포함)
     */
    public List<AssignmentCandidateDto> getCandidates(Long orderId) {
        List<Long> teamMemberIds = hrClient.getTeamMembers().stream()
                .map(member -> member.getEmployeeId())
                .toList();
        if (teamMemberIds.isEmpty()) {
            return List.of();
        }
        return assignmentMapper.findCandidatesByEmployeeIds(teamMemberIds, orderId);
    }

    /**
     * 오늘 배정 수, 미배정 주문 수, 배정 정확도 등 배정 현황 요약을 조회한다.
     *
     * @return 배정 현황 요약 DTO
     */
    public AssignmentSummaryDto getSummary() {
        return assignmentMapper.findSummary();
    }

    /**
     * 라인별 배정 타임라인(기술자, 날짜, 주문 상태)을 조회한다.
     *
     * @return 배정 타임라인 목록
     */
    public List<AssignmentTimelineDto> getTimeline() {
        return assignmentMapper.findTimeline();
    }

    /**
     * 라인별 기술자 티어 분포 및 권장 배치 인원 포함 재조정 현황을 조회한다.
     *
     * @return 라인별 재조정 현황 목록
     */
    public List<AssignmentRebalanceDto> getRebalance() {
        return assignmentMapper.findRebalance();
    }
}

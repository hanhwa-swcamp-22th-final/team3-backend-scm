package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentCandidateDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentTimelineDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.AssignmentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 배정 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>GET /assignments/{matchingRecordId} - 배정 상세 단건 조회</li>
 *   <li>GET /assignments/candidates         - 배정 후보 기술자 목록 조회</li>
 *   <li>GET /assignments/summary            - 배정 현황 요약 조회</li>
 *   <li>GET /assignments/timeline           - 라인별 배정 타임라인 조회</li>
 *   <li>GET /assignments/rebalance          - 라인별 기술자 재조정 현황 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('TL')")
public class AssignmentQueryController {

    private final AssignmentQueryService assignmentQueryService;

    /**
     * 배정 기록 ID로 배정 상세 정보를 단건 조회한다.
     *
     * @param matchingRecordId 조회할 배정 기록 PK
     * @return 배정 상세 DTO
     */
    @GetMapping("/assignments/{matchingRecordId}")
    public ResponseEntity<ApiResponse<AssignmentDetailDto>> getAssignment(
            @PathVariable Long matchingRecordId) {
        AssignmentDetailDto detail = assignmentQueryService.getAssignment(matchingRecordId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    /**
     * 배정 가능한 기술자 정보 목록을 조회한다.
     *
     * @return 후보 기술자 목록 (보유 티어, OCSA 점수, 적합도 포함)
     */
    @GetMapping("/assignments/candidates")
    public ResponseEntity<ApiResponse<List<AssignmentCandidateDto>>> getCandidates(
            @RequestParam(required = false) Long orderId) {
        List<AssignmentCandidateDto> candidates = assignmentQueryService.getCandidates(orderId);
        return ResponseEntity.ok(ApiResponse.success(candidates));
    }

    /**
     * 오늘 배정 수, 미배정 주문 수, 배정 정확도 등 배정 현황 요약을 조회한다.
     *
     * @return 배정 현황 요약 DTO
     */
    @GetMapping("/assignments/summary")
    public ResponseEntity<ApiResponse<AssignmentSummaryDto>> getSummary() {
        AssignmentSummaryDto summary = assignmentQueryService.getSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 라인별 배정 타임라인(기술자, 날짜, 주문 상태)을 조회한다.
     *
     * @return 배정 타임라인 목록
     */
    @GetMapping("/assignments/timeline")
    public ResponseEntity<ApiResponse<List<AssignmentTimelineDto>>> getTimeline() {
        List<AssignmentTimelineDto> timeline = assignmentQueryService.getTimeline();
        return ResponseEntity.ok(ApiResponse.success(timeline));
    }

    /**
     * 라인별 기술자 티어 분포 및 권장 배치 인원 포함 재조정 현황을 조회한다.
     *
     * @return 라인별 재조정 현황 목록
     */
    @GetMapping("/assignments/rebalance")
    public ResponseEntity<ApiResponse<List<AssignmentRebalanceDto>>> getRebalance() {
        List<AssignmentRebalanceDto> rebalance = assignmentQueryService.getRebalance();
        return ResponseEntity.ok(ApiResponse.success(rebalance));
    }
}

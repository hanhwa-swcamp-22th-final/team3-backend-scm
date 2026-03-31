package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.LineQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 공장 라인 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>GET /lines/summary          - 전체 라인 주문 처리 요약 조회</li>
 *   <li>GET /lines/{lineId}/status  - 특정 라인 실시간 운영 현황 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class LineQueryController {

    private final LineQueryService lineQueryService;

    /**
     * 전체 공장 라인의 주문 처리 요약(총 주문 수, 완료 수, 달성률)을 조회한다.
     *
     * @return 라인별 요약 목록
     */
    @GetMapping("/lines/summary")
    public ResponseEntity<ApiResponse<List<LineSummaryDto>>> getLinesSummary() {
        List<LineSummaryDto> summary = lineQueryService.getLinesSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 특정 라인의 실시간 운영 현황(배정 기술자 수, 진행 주문 수, 설비 가동률)을 조회한다.
     *
     * @param lineId 조회할 라인 ID
     * @return 라인 운영 현황 DTO
     */
    @GetMapping("/lines/{lineId}/status")
    public ResponseEntity<ApiResponse<LineStatusDto>> getLineStatus(@PathVariable Long lineId) {
        LineStatusDto status = lineQueryService.getLineStatus(lineId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }
}

package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
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
 * ?� 리더(TL) 권한??공장 ?�인 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * ?�공 ?�드?�인??
 * <ul>
 *   <li>GET /lines/summary          - ?�체 ?�인 주문 처리 ?�약 조회</li>
 *   <li>GET /lines/{lineId}/status  - ?�정 ?�인 ?�시�??�영 ?�황 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class LineQueryController {

    private final LineQueryService lineQueryService;

    /**
     * ?�체 공장 ?�인??주문 처리 ?�약(�?주문 ?? ?�료 ?? ?�성�???조회?�다.
     *
     * @return ?�인�??�약 목록
     */
    @GetMapping("/lines/summary")
    public ResponseEntity<ApiResponse<List<LineSummaryDto>>> getLinesSummary() {
        List<LineSummaryDto> summary = lineQueryService.getLinesSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * ?�정 ?�인???�시�??�영 ?�황(배정 기술???? 진행 주문 ?? ?�비 가?�률)??조회?�다.
     *
     * @param lineId 조회???�인 ID
     * @return ?�인 ?�영 ?�황 DTO
     */
    @GetMapping("/lines/{lineId}/status")
    public ResponseEntity<ApiResponse<LineStatusDto>> getLineStatus(@PathVariable Long lineId) {
        LineStatusDto status = lineQueryService.getLineStatus(lineId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }
}

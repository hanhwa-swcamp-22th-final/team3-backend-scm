package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.TechnicianDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.TechnicianQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 기술자 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>GET /technicians - 배정 가능한 기술자 목록 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class TechnicianQueryController {

    private final TechnicianQueryService technicianQueryService;

    /**
     * 배정 가능한 전체 기술자 목록을 조회한다.
     * 각 기술자의 이름, 역량 티어, OCSA 점수, 적합도를 포함한다.
     *
     * @return 기술자 목록
     */
    @GetMapping("/technicians")
    public ResponseEntity<ApiResponse<List<TechnicianDto>>> getTechnicians() {
        List<TechnicianDto> technicians = technicianQueryService.getTechnicians();
        return ResponseEntity.ok(ApiResponse.success(technicians));
    }
}

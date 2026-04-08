package com.ohgiraffers.team3backendscm.scm.query.controller.tl;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.query.dto.request.OrderQueryRequest;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OcsaSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderReadDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.service.tl.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 팀 리더(TL) 권한의 주문 조회 REST 컨트롤러.
 * 기본 경로: /api/v1/scm
 * <p>
 * 제공 엔드포인트:
 * <ul>
 *   <li>GET /orders                  - 조건 필터링된 주문 목록 조회</li>
 *   <li>GET /orders/urgent           - 납기 임박 긴급 주문 목록 조회</li>
 *   <li>GET /orders/unassigned       - 미배정(ANALYZED 상태) 주문 목록 조회</li>
 *   <li>GET /orders/summary          - 주문 현황 요약 조회</li>
 *   <li>GET /orders/ocsa/summary     - OCSA 분석 현황 요약 조회</li>
 *   <li>GET /orders/{orderId}        - 주문 상세 조회</li>
 *   <li>GET /orders/{orderId}/ocsa   - OCSA 난이도 분석 결과 조회</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    /**
     * 상태·난이도 등급 필터, 키워드, 페이징 조건에 따라 주문 목록을 조회한다.
     *
     * @param request 쿼리 파라미터로 바인딩되는 검색 조건 DTO
     * @return 조건에 맞는 주문 요약 목록
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderReadDto>>> getOrders(
            @ModelAttribute OrderQueryRequest request) {
        List<OrderReadDto> orders = orderQueryService.getOrders(request);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 납기 3일 이내의 긴급 주문 목록을 조회한다.
     *
     * @return 긴급 주문 목록
     */
    @GetMapping("/orders/urgent")
    public ResponseEntity<ApiResponse<List<OrderReadDto>>> getUrgentOrders() {
        List<OrderReadDto> orders = orderQueryService.getUrgentOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * OCSA 분석 완료 후 기술자가 배정되지 않은 주문(ANALYZED 상태) 목록을 조회한다.
     *
     * @return 미배정 주문 목록
     */
    @GetMapping("/orders/unassigned")
    public ResponseEntity<ApiResponse<List<OrderReadDto>>> getUnassignedOrders() {
        List<OrderReadDto> orders = orderQueryService.getUnassignedOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 전체 주문 현황 집계 요약(총 수, 진행 중, 납기 위험, 달성률, 칸반 컬럼별 카운트)을 조회한다.
     *
     * @return 주문 현황 요약 DTO
     */
    @GetMapping("/orders/summary")
    public ResponseEntity<ApiResponse<OrderSummaryDto>> getOrderSummary() {
        OrderSummaryDto summary = orderQueryService.getOrderSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * OCSA 분석 현황 요약(분석 주문 수, 평균 난이도 점수, 최고 난이도 등급)을 조회한다.
     *
     * @return OCSA 요약 DTO
     */
    @GetMapping("/orders/ocsa/summary")
    public ResponseEntity<ApiResponse<OcsaSummaryDto>> getOcsaSummary() {
        OcsaSummaryDto summary = orderQueryService.getOcsaSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 주문 ID로 주문 상세 정보를 조회한다.
     *
     * @param orderId 조회할 주문 PK
     * @return 주문 상세 DTO
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailDto>> getOrder(@PathVariable Long orderId) {
        OrderDetailDto order = orderQueryService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * 특정 주문의 OCSA 난이도 분석 결과(V1~V4, α, 점수, 등급, 추천 기술자)를 조회한다.
     *
     * @param orderId 조회할 주문 PK
     * @return OCSA 분석 결과 DTO
     */
    @GetMapping("/orders/{orderId}/ocsa")
    public ResponseEntity<ApiResponse<OrderOcsaDto>> getOrderOcsa(@PathVariable Long orderId) {
        OrderOcsaDto ocsa = orderQueryService.getOrderOcsa(orderId);
        return ResponseEntity.ok(ApiResponse.success(ocsa));
    }
}

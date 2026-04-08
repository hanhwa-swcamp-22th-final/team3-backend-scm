package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.OrderCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 모듈이 호출하는 주문(Order) 등록·수정·삭제 REST 컨트롤러.
 * 기본 경로: /api/v1/scm/admin/orders
 * <p>
 * SCM 워크플로우(조회·상태 변경)는 별도 TL/Worker 컨트롤러에서 처리한다.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm/admin/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    /**
     * 주문을 등록한다. 초기 상태는 REGISTERED로 고정된다.
     *
     * @param request 주문 정보를 담은 요청 DTO
     * @return 생성된 주문 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long orderId = orderCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderId));
    }

    /**
     * 주문 기본 정보를 수정한다. REGISTERED 상태인 주문만 허용된다.
     *
     * @param orderId 수정할 주문 ID
     * @param request 변경할 정보를 담은 요청 DTO
     * @return 성공 응답 (data = null)
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        orderCommandService.update(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 주문을 삭제한다. REGISTERED 상태인 주문만 허용된다.
     *
     * @param orderId 삭제할 주문 ID
     * @return 성공 응답 (data = null)
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderCommandService.delete(orderId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

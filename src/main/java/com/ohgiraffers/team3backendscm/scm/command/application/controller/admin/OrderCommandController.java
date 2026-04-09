package com.ohgiraffers.team3backendscm.scm.command.application.controller.admin;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderCreateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.OrderUpdateRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.admin.OrderCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 모듈에서 호출하는 주문 등록, 수정, 삭제용 REST 컨트롤러이다.
 * 기본 경로는 /api/v1/scm/admin/orders 이다.
 */
@RestController
@RequestMapping("/api/v1/scm/admin/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final OrderCommandService orderCommandService;

    /**
     * 주문을 등록한다.
     *
     * @param request 주문 등록 요청 DTO
     * @return 생성된 주문 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long orderId = orderCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(orderId));
    }

    /**
     * 주문 기본 정보를 수정한다.
     *
     * @param orderId 수정할 주문 ID
     * @param request 주문 수정 요청 DTO
     * @return 성공 응답
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> updateOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateRequest request) {
        orderCommandService.update(orderId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 주문을 삭제한다.
     *
     * @param orderId 삭제할 주문 ID
     * @return 성공 응답
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderCommandService.delete(orderId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}